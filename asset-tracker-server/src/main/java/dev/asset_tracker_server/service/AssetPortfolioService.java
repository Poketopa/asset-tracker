package dev.asset_tracker_server.service;

import dev.asset_tracker_server.api.dto.AssetEvaluationDto;
import dev.asset_tracker_server.api.dto.PortfolioHistoryDto;
import dev.asset_tracker_server.api.dto.PortfolioSummaryDto;
import dev.asset_tracker_server.api.dto.TickerPriceDto;
import dev.asset_tracker_server.entity.Asset;
import dev.asset_tracker_server.entity.AssetType;
import dev.asset_tracker_server.entity.ExchangeRate;
import dev.asset_tracker_server.entity.User;
import dev.asset_tracker_server.fetcher.PriceFetchManager;
import dev.asset_tracker_server.repository.AssetRepository;
import dev.asset_tracker_server.repository.AssetSnapshotRepository;
import dev.asset_tracker_server.repository.ExchangeRateRepository;
import dev.asset_tracker_server.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class AssetPortfolioService {

    private final AssetRepository assetRepository;
    private final PriceFetchManager priceFetchManager;
    private final ExchangeRateRepository exchangeRateRepository;
    private final UserRepository userRepository;
    private final AssetSnapshotRepository assetSnapshotRepository;

    public Map<String, BigDecimal> calculatePortfolioValue(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("사용자 없음"));
        List<Asset> assets = assetRepository.findByUser(user);

        // 최신 환율 정보 조회
        BigDecimal usdToKrw = exchangeRateRepository.findLatestByType("USD/KRW", PageRequest.of(0, 1))
                .stream().findFirst().orElseThrow(() -> new RuntimeException("USD/KRW 환율 정보 없음")).getUsdToKrw();

        BigDecimal usdtToKrw = exchangeRateRepository.findLatestByType("USDT/KRW", PageRequest.of(0, 1))
                .stream().findFirst().orElseThrow(() -> new RuntimeException("USDT/KRW 환율 정보 없음")).getUsdtToKrw();

        BigDecimal totalUsd = BigDecimal.ZERO;
        BigDecimal totalKrw = BigDecimal.ZERO;

        for (Asset asset : assets) {
            BigDecimal quantity = asset.getQuantity();
            String symbol = asset.getSymbol();
            
            BigDecimal priceUsd;
            BigDecimal priceKrw;

            // 가격 조회 및 계산
            if (asset.getAssetType() == AssetType.CASH) {
                String baseCurrency = getBaseCurrencyFromSymbol(symbol);
                if (baseCurrency.equals("KRW")) {
                    priceUsd = BigDecimal.ONE.divide(usdToKrw, 8, RoundingMode.HALF_UP);
                    priceKrw = BigDecimal.ONE;
                } else if (baseCurrency.equals("USD")) {
                    priceUsd = BigDecimal.ONE;
                    priceKrw = usdToKrw;
                } else if (baseCurrency.equals("USDT")) {
                    priceUsd = BigDecimal.ONE;
                    priceKrw = usdtToKrw;
                } else {
                    continue;
                }
            } else if (asset.getAssetType() == AssetType.STOCK) {
                // 주식은 finnhub에서 조회
                var dto = priceFetchManager.fetch("finnhub", symbol);
                priceUsd = dto.price();
                priceKrw = switch (dto.currency()) {
                    case "USDT" -> priceUsd.multiply(usdtToKrw);
                    case "USD" -> priceUsd.multiply(usdToKrw);
                    case "KRW" -> priceUsd;
                    default -> throw new IllegalArgumentException("지원하지 않는 통화: " + dto.currency());
                };
            } else {
                // 암호화폐는 우선순위 기반으로 조회 (binance → bybit → okx → gateio)
                var dto = priceFetchManager.fetchWithPriority(symbol);
                priceUsd = dto.price();
                priceKrw = switch (dto.currency()) {
                    case "USDT" -> priceUsd.multiply(usdtToKrw);
                    case "USD" -> priceUsd.multiply(usdToKrw);
                    case "KRW" -> priceUsd;
                    default -> throw new IllegalArgumentException("지원하지 않는 통화: " + dto.currency());
                };
            }

            // 총 가치 계산
            BigDecimal assetValueUsd = priceUsd.multiply(quantity);
            BigDecimal assetValueKrw = priceKrw.multiply(quantity);

            totalUsd = totalUsd.add(assetValueUsd);
            totalKrw = totalKrw.add(assetValueKrw);
        }

        return Map.of("USD", totalUsd, "KRW", totalKrw);
    }

    /**
     * 심볼에서 기본 통화를 추출하는 메서드
     * 패턴 매칭을 통한 기본 통화 결정
     */
    private String getBaseCurrencyFromSymbol(String symbol) {
        // 패턴 매칭
        if (symbol.endsWith("USDT")) return "USDT";
        if (symbol.endsWith("USD")) return "USD";
        if (symbol.equals("KRW")) return "KRW";
        
        // 기본값 (대부분의 암호화폐는 USDT 페어)
        return "USDT";
    }

    private BigDecimal getExchangeRate(String type) {
        // 환율 조회 로직 (실제 구현 필요)
        return switch (type) {
            case "USD/KRW" -> new BigDecimal("1300");
            case "USDT/KRW" -> new BigDecimal("1300");
            default -> BigDecimal.ONE;
        };
    }

    public PortfolioSummaryDto getPortfolioSummary(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("사용자 없음"));
        List<Asset> assets = assetRepository.findByUser(user);

        BigDecimal totalUsd = BigDecimal.ZERO;
        BigDecimal totalKrw = BigDecimal.ZERO;

        for (Asset asset : assets) {
            BigDecimal assetValue = asset.getQuantity();
            String symbol = asset.getSymbol();
            
            // SymbolMapping에서 기본 통화 정보 가져오기
            String baseCurrency = getBaseCurrencyFromSymbol(symbol);
            
            BigDecimal rateType = switch (baseCurrency) {
                case "USD" -> getExchangeRate("USD/KRW");
                case "USDT" -> getExchangeRate("USDT/KRW");
                case "KRW" -> null; // KRW라면 변환 불필요
                default -> throw new RuntimeException("지원하지 않는 통화: " + baseCurrency);
            };

            BigDecimal valueKrw;
            if (rateType != null) {
                ExchangeRate rate = exchangeRateRepository.findLatestByType(rateType.toString(), PageRequest.of(0, 1))
                        .stream().findFirst()
                        .orElseThrow(() -> new RuntimeException("환율 정보 없음"));
                valueKrw = assetValue.multiply(rate.getRate());
            } else {
                valueKrw = assetValue;
            }

            totalKrw = totalKrw.add(valueKrw);
        }

        return new PortfolioSummaryDto(totalUsd, totalKrw);
    }

    public List<PortfolioHistoryDto> getPortfolioHistory(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("사용자 없음"));

        List<Object[]> result = assetSnapshotRepository.findDailyPortfolioHistory(user);

        return result.stream()
                .map(row -> new PortfolioHistoryDto(
                        (LocalDate) row[0],
                        (BigDecimal) row[1],
                        (BigDecimal) row[2]
                ))
                .toList();
    }

    public List<AssetEvaluationDto> evaluatePortfolio(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("사용자 없음"));
        List<Asset> assets = assetRepository.findByUser(user);

        // 최신 환율 정보 조회
        BigDecimal usdToKrw = exchangeRateRepository.findLatestByType("USD/KRW", PageRequest.of(0, 1))
                .stream().findFirst().orElseThrow(() -> new RuntimeException("환율 정보 없음")).getUsdToKrw();

        BigDecimal usdtToKrw = exchangeRateRepository.findLatestByType("USDT/KRW", PageRequest.of(0, 1))
                .stream().findFirst().orElseThrow(() -> new RuntimeException("환율 정보 없음")).getUsdtToKrw();

        List<AssetEvaluationDto> results = new ArrayList<>();

        for (Asset asset : assets) {
            BigDecimal quantity = asset.getQuantity();
            String symbol = asset.getSymbol();
            
            BigDecimal priceUsd;
            BigDecimal priceKrw;
            String exchange;

            // 가격 조회
            if (asset.getAssetType() == AssetType.CASH) {
                String baseCurrency = getBaseCurrencyFromSymbol(symbol);
                if (baseCurrency.equals("KRW")) {
                    priceUsd = usdToKrw.divide(usdToKrw, 2, RoundingMode.HALF_UP);
                    priceKrw = BigDecimal.ONE;
                    exchange = "cash";
                } else if (baseCurrency.equals("USD")) {
                    priceUsd = BigDecimal.ONE;
                    priceKrw = usdToKrw;
                    exchange = "cash";
                } else if (baseCurrency.equals("USDT")) {
                    priceUsd = BigDecimal.ONE;
                    priceKrw = usdtToKrw;
                    exchange = "cash";
                } else {
                    continue;
                }
            } else if (asset.getAssetType() == AssetType.STOCK) {
                // 주식은 finnhub에서 조회
                var dto = priceFetchManager.fetch("finnhub", symbol);
                priceUsd = dto.price();
                priceKrw = switch (dto.currency()) {
                    case "USDT" -> priceUsd.multiply(usdtToKrw);
                    case "USD" -> priceUsd.multiply(usdToKrw);
                    case "KRW" -> priceUsd;
                    default -> throw new IllegalArgumentException("지원하지 않는 통화: " + dto.currency());
                };
                exchange = "finnhub";
            } else {
                // 암호화폐는 우선순위 기반으로 조회 (binance → bybit → okx → gateio)
                var dto = priceFetchManager.fetchWithPriority(symbol);
                priceUsd = dto.price();
                priceKrw = switch (dto.currency()) {
                    case "USDT" -> priceUsd.multiply(usdtToKrw);
                    case "USD" -> priceUsd.multiply(usdToKrw);
                    case "KRW" -> priceUsd;
                    default -> throw new IllegalArgumentException("지원하지 않는 통화: " + dto.currency());
                };
                exchange = dto.exchange(); // 실제 조회된 거래소 정보 사용
            }

            BigDecimal totalUsd = priceUsd.multiply(quantity);
            BigDecimal totalKrw = priceKrw.multiply(quantity);

            results.add(AssetEvaluationDto.builder()
                    .symbol(symbol)
                    .quantity(quantity)
                    .unitPriceUsd(priceUsd)
                    .unitPriceKrw(priceKrw)
                    .totalValueUsd(totalUsd)
                    .totalValueKrw(totalKrw)
                    .currency(exchange)
                    .build());
        }

        return results;
    }
}

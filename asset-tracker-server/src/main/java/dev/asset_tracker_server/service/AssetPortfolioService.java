package dev.asset_tracker_server.service;

import dev.asset_tracker_server.api.dto.AssetEvaluationDto;
import dev.asset_tracker_server.api.dto.PortfolioHistoryDto;
import dev.asset_tracker_server.api.dto.PortfolioSummaryDto;
import dev.asset_tracker_server.api.dto.TickerPriceDto;
import dev.asset_tracker_server.entity.Asset;
import dev.asset_tracker_server.entity.AssetType;
import dev.asset_tracker_server.entity.ExchangeRate;
import dev.asset_tracker_server.entity.User;
import dev.asset_tracker_server.entity.SymbolMapping;
import dev.asset_tracker_server.fetcher.PriceFetchManager;
import dev.asset_tracker_server.repository.AssetRepository;
import dev.asset_tracker_server.repository.AssetSnapshotRepository;
import dev.asset_tracker_server.repository.ExchangeRateRepository;
import dev.asset_tracker_server.repository.SymbolMappingRepository;
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
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class AssetPortfolioService {

    private final AssetRepository assetRepository;
    private final PriceFetchManager priceFetchManager;
    private final ExchangeRateRepository exchangeRateRepository;
    private final SymbolMappingRepository symbolMappingRepository;
    private final UserRepository userRepository;
    private final AssetSnapshotRepository assetSnapshotRepository;

    public Map<String, BigDecimal> calculatePortfolioValue(UUID userId) {
        User user = User.builder().id(userId).build();
        List<Asset> assets = assetRepository.findByUser(user);

        BigDecimal totalUsd = BigDecimal.ZERO;
        BigDecimal totalKrw = BigDecimal.ZERO;

        // 최신 환율 정보
        BigDecimal usdToKrw = getExchangeRate("USD/KRW");
        BigDecimal usdtToKrw = getExchangeRate("USDT/KRW");

        for (Asset asset : assets) {
            SymbolMapping mapping = symbolMappingRepository.findById(asset.getSymbol())
                    .orElseThrow(() -> new IllegalArgumentException("심볼 매핑 정보 없음"));
            String exchange = asset.getAssetType() == AssetType.STOCK
                    ? "finnhub"
                    : mapping.getExchange().name().toLowerCase();
            String symbol = asset.getSymbol();

            try {
                TickerPriceDto priceDto = priceFetchManager.fetch(exchange, symbol);
                BigDecimal price = priceDto.price();
                BigDecimal quantity = asset.getQuantity();
                BigDecimal valueUsd = price.multiply(quantity);

                totalUsd = totalUsd.add(valueUsd);

                // 환산
                BigDecimal rate = priceDto.currency().equals("USDT") ? usdtToKrw : usdToKrw;
                totalKrw = totalKrw.add(valueUsd.multiply(rate));
            } catch (Exception e) {
                log.warn("가격 조회 실패: {} - {}", symbol, e.getMessage());
            }
        }

        return Map.of(
                "totalUsd", totalUsd,
                "totalKrw", totalKrw
        );
    }

    private BigDecimal getExchangeRate(String type) {
        return exchangeRateRepository.findLatestByType(type, PageRequest.of(0, 1))
                .stream()
                .findFirst()
                .map(rate -> rate.getRate())
                .orElseThrow(() -> new IllegalStateException("환율 정보 없음: " + type));
    }

    public PortfolioSummaryDto getPortfolioSummary(UUID userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("사용자 없음"));

        List<Asset> assets = assetRepository.findByUser(user);

        BigDecimal totalUsd = BigDecimal.ZERO;
        BigDecimal totalKrw = BigDecimal.ZERO;

        for (Asset asset : assets) {
            String symbol = asset.getSymbol();
            BigDecimal quantity = asset.getQuantity();
            AssetType type = asset.getAssetType();

            // 심볼 정보 매핑
            SymbolMapping mapping = symbolMappingRepository.findById(symbol)
                    .orElseThrow(() -> new IllegalArgumentException("심볼 매핑 없음"));

            // 가격 조회
            TickerPriceDto priceDto = priceFetchManager.fetch(mapping.getExchange().name(), mapping.getExchangeSymbol());
            BigDecimal price = priceDto.price();
            String currency = priceDto.currency();

            BigDecimal assetValue = price.multiply(quantity);
            totalUsd = totalUsd.add(assetValue);

            // 환율 조회
            String rateType = switch (currency) {
                case "USD" -> "USD/KRW";
                case "USDT" -> "USDT/KRW";
                case "KRW" -> null; // KRW라면 변환 불필요
                default -> throw new RuntimeException("지원하지 않는 통화: " + currency);
            };

            BigDecimal valueKrw;
            if (rateType != null) {
                ExchangeRate rate = exchangeRateRepository.findLatestByType(rateType, PageRequest.of(0, 1))
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

    public List<PortfolioHistoryDto> getPortfolioHistory(UUID userId) {
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

    public List<AssetEvaluationDto> evaluatePortfolio(UUID userId) {
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
            // 거래소명 결정
            String exchange;
            if (asset.getAssetType() == AssetType.STOCK) {
                exchange = "finnhub";
            } else {
                SymbolMapping mapping = symbolMappingRepository.findById(symbol)
                        .orElseThrow(() -> new IllegalArgumentException("심볼 매핑 정보 없음"));
                exchange = mapping.getExchange().name().toLowerCase();
            }

            BigDecimal priceUsd;
            BigDecimal priceKrw;

            // 가격 조회
            if (asset.getAssetType() == AssetType.CASH) {
                if (symbol.equalsIgnoreCase("KRW")) {
                    priceUsd = usdToKrw.divide(usdToKrw, 2, RoundingMode.HALF_UP);
                    priceKrw = BigDecimal.ONE;
                } else if (symbol.equalsIgnoreCase("USD")) {
                    priceUsd = BigDecimal.ONE;
                    priceKrw = usdToKrw;
                } else if (symbol.equalsIgnoreCase("USDT")) {
                    priceUsd = BigDecimal.ONE;
                    priceKrw = usdtToKrw;
                } else {
                    continue;
                }
            } else {
                var dto = priceFetchManager.fetch(exchange, symbol);
                priceUsd = dto.price();
                priceKrw = switch (dto.currency()) {
                    case "USDT" -> priceUsd.multiply(usdtToKrw);
                    case "USD" -> priceUsd.multiply(usdToKrw);
                    case "KRW" -> priceUsd;
                    default -> throw new IllegalArgumentException("지원하지 않는 통화: " + dto.currency());
                };
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

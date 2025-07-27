package dev.asset_tracker_server.service;

import dev.asset_tracker_server.api.dto.AssetEvaluationDto;
import dev.asset_tracker_server.api.dto.TickerPriceDto;
import dev.asset_tracker_server.entity.Asset;
import dev.asset_tracker_server.entity.AssetType;
import dev.asset_tracker_server.entity.ExchangeRate;
import dev.asset_tracker_server.entity.User;
import dev.asset_tracker_server.fetcher.PriceFetchManager;
import dev.asset_tracker_server.repository.AssetRepository;
import dev.asset_tracker_server.repository.ExchangeRateRepository;
import dev.asset_tracker_server.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AssetPortfolioDetailService {

    private final AssetRepository assetRepository;
    private final UserRepository userRepository;
    private final ExchangeRateRepository exchangeRateRepository;
    private final PriceFetchManager priceFetchManager;

    public List<AssetEvaluationDto> getDetailedEvaluation(UUID userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("유저가 존재하지 않습니다"));

        List<Asset> assets = assetRepository.findByUser(user);

        ExchangeRate usdRate = exchangeRateRepository.findLatestByType("USD/KRW", PageRequest.of(0, 1))
                .stream().findFirst().orElseThrow();
        ExchangeRate usdtRate = exchangeRateRepository.findLatestByType("USDT/KRW", PageRequest.of(0, 1))
                .stream().findFirst().orElseThrow();

        return assets.stream().map(asset -> {
            BigDecimal unitPriceUsd = BigDecimal.ZERO;
            BigDecimal unitPriceKrw = BigDecimal.ZERO;

            String currency = "USD";

            if (asset.getAssetType() == AssetType.CASH) {
                // USD, KRW, USDT 현금형
                switch (asset.getSymbol().toUpperCase()) {
                    case "KRW" -> {
                        unitPriceKrw = BigDecimal.ONE;
                        unitPriceUsd = BigDecimal.ONE.divide(usdRate.getUsdToKrw(), 6, BigDecimal.ROUND_HALF_UP);
                        currency = "KRW";
                    }
                    case "USD" -> {
                        unitPriceUsd = BigDecimal.ONE;
                        unitPriceKrw = usdRate.getUsdToKrw();
                        currency = "USD";
                    }
                    case "USDT" -> {
                        unitPriceUsd = BigDecimal.ONE;
                        unitPriceKrw = usdtRate.getUsdtToKrw();
                        currency = "USDT";
                    }
                }
            } else {
                // 실시간 가격 조회
                String exchange = (asset.getAssetType() == AssetType.STOCK) ? "finnhub" : asset.getExchange().name().toLowerCase();
                TickerPriceDto priceDto = priceFetchManager.fetch(exchange, asset.getSymbol());
                unitPriceUsd = priceDto.price();
                unitPriceKrw = priceDto.price().multiply(
                        priceDto.currency().equalsIgnoreCase("USDT") ?
                                usdtRate.getUsdtToKrw() : usdRate.getUsdToKrw());
                currency = priceDto.currency();
            }

            BigDecimal totalUsd = unitPriceUsd.multiply(asset.getQuantity());
            BigDecimal totalKrw = unitPriceKrw.multiply(asset.getQuantity());

            return AssetEvaluationDto.builder()
                    .symbol(asset.getSymbol())
                    .quantity(asset.getQuantity())
                    .unitPriceUsd(unitPriceUsd)
                    .unitPriceKrw(unitPriceKrw)
                    .totalValueUsd(totalUsd)
                    .totalValueKrw(totalKrw)
                    .currency(currency)
                    .build();

        }).collect(Collectors.toList());
    }
}

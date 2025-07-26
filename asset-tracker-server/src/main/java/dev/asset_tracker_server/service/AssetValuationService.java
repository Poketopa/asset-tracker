package dev.asset_tracker_server.service;

import dev.asset_tracker_server.api.dto.PortfolioValuationDto;
import dev.asset_tracker_server.api.dto.TickerPriceDto;
import dev.asset_tracker_server.entity.Asset;
import dev.asset_tracker_server.entity.SymbolMapping;
import dev.asset_tracker_server.entity.User;
import dev.asset_tracker_server.fetcher.PriceFetchManager;
import dev.asset_tracker_server.repository.AssetRepository;
import dev.asset_tracker_server.repository.ExchangeRateRepository;
import dev.asset_tracker_server.repository.SymbolMappingRepository;
import dev.asset_tracker_server.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class AssetValuationService {

    private final UserRepository userRepository;
    private final AssetRepository assetRepository;
    private final SymbolMappingRepository symbolMappingRepository;
    private final ExchangeRateRepository exchangeRateRepository;
    private final PriceFetchManager priceFetchManager;

    public PortfolioValuationDto getPortfolioSummary(UUID userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("유저를 찾을 수 없습니다."));

        List<Asset> assets = assetRepository.findByUser(user);
        BigDecimal totalUsd = BigDecimal.ZERO;
        BigDecimal totalKrw = BigDecimal.ZERO;

        for (Asset asset : assets) {
            SymbolMapping mapping = symbolMappingRepository.findById(asset.getSymbol())
                    .orElseThrow(() -> new IllegalArgumentException("심볼 매핑이 없습니다: " + asset.getSymbol()));

            TickerPriceDto dto = priceFetchManager.fetch(mapping.getExchange().name(), mapping.getExchangeSymbol());
            BigDecimal quantity = asset.getQuantity();
            BigDecimal pricePerUnit = dto.price();
            BigDecimal valueUsd = pricePerUnit.multiply(quantity);

            BigDecimal rate = switch (dto.currency()) {
                case "USDT" -> exchangeRateRepository.findLatestByType("USDT/KRW", PageRequest.of(0, 1)).get(0).getRate();
                case "USD" -> exchangeRateRepository.findLatestByType("USD/KRW", PageRequest.of(0, 1)).get(0).getRate();
                default -> BigDecimal.ONE;
            };

            BigDecimal valueKrw = valueUsd.multiply(rate);

            totalUsd = totalUsd.add(valueUsd);
            totalKrw = totalKrw.add(valueKrw);
        }

        return PortfolioValuationDto.builder()
                .userId(userId)
                .totalValueUsd(totalUsd)
                .totalValueKrw(totalKrw)
                .asOf(Instant.now())
                .build();
    }
}

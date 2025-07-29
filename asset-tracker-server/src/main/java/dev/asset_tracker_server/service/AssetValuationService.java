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

@Service
@RequiredArgsConstructor
@Slf4j
public class AssetValuationService {

    private final UserRepository userRepository;
    private final AssetRepository assetRepository;
    private final SymbolMappingRepository symbolMappingRepository;
    private final ExchangeRateRepository exchangeRateRepository;
    private final PriceFetchManager priceFetchManager;

    public PortfolioValuationDto getPortfolioSummary(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("사용자 없음"));
        List<Asset> assets = assetRepository.findByUser(user);

        BigDecimal totalValueUsd = BigDecimal.ZERO;
        BigDecimal totalValueKrw = BigDecimal.ZERO;

        for (Asset asset : assets) {
            // 실제 가격 조회 및 계산 로직 필요
            totalValueUsd = totalValueUsd.add(asset.getQuantity());
            totalValueKrw = totalValueKrw.add(asset.getQuantity());
        }

        return PortfolioValuationDto.builder()
                .userId(userId)
                .totalValueUsd(totalValueUsd)
                .totalValueKrw(totalValueKrw)
                .build();
    }
}

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
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AssetPortfolioDetailService {

    private final AssetRepository assetRepository;
    private final UserRepository userRepository;
    private final ExchangeRateRepository exchangeRateRepository;
    private final PriceFetchManager priceFetchManager;

    public List<AssetEvaluationDto> getDetailedEvaluation(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("사용자 없음"));
        List<Asset> assets = assetRepository.findByUser(user);

        return assets.stream()
                .map(asset -> AssetEvaluationDto.builder()
                        .symbol(asset.getSymbol())
                        .quantity(asset.getQuantity())
                        .unitPriceUsd(BigDecimal.ZERO) // 실제 가격 조회 로직 필요
                        .unitPriceKrw(BigDecimal.ZERO) // 실제 가격 조회 로직 필요
                        .totalValueUsd(BigDecimal.ZERO) // 실제 계산 로직 필요
                        .totalValueKrw(BigDecimal.ZERO) // 실제 계산 로직 필요
                        .currency("KRW")
                        .build())
                .toList();
    }
}

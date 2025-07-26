package dev.asset_tracker_server.service;

import dev.asset_tracker_server.entity.Asset;
import dev.asset_tracker_server.entity.User;
import dev.asset_tracker_server.repository.AssetRepository;
import dev.asset_tracker_server.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;
import dev.asset_tracker_server.entity.AssetType;

@Service
@RequiredArgsConstructor
public class AssetService {

    private final AssetRepository assetRepository;
    private final UserRepository userRepository;

    public static record AssetDto(String symbol, BigDecimal quantity, AssetType assetType, boolean isKimchi) {}

    public List<AssetDto> getAssetsByUser(UUID userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("사용자 없음"));

        return assetRepository.findByUser(user).stream()
                .map(asset -> new AssetDto(asset.getSymbol(), asset.getQuantity(), asset.getAssetType(), asset.isKimchi()))
                .toList();
    }

    public void saveAsset(UUID userId, Asset asset) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("사용자 없음"));

        // 중복 심볼 방지
        boolean exists = assetRepository.findByUser(user).stream()
                .anyMatch(a -> a.getSymbol().equals(asset.getSymbol()));
        if (exists) {
            throw new IllegalArgumentException("이미 등록된 자산 심볼입니다.");
        }
        // 수량 음수 방지
        if (asset.getQuantity() == null || asset.getQuantity().compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("자산 수량은 0 이상이어야 합니다.");
        }
        // 자산 유형 필수
        if (asset.getAssetType() == null) {
            throw new IllegalArgumentException("자산 유형은 필수입니다.");
        }

        asset.setUser(user);
        assetRepository.save(asset);
    }
}

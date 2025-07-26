package dev.asset_tracker_server.api.dto;

import dev.asset_tracker_server.entity.AssetType;
import lombok.Builder;

import java.math.BigDecimal;

@Builder
public record AssetDto(
        String symbol,
        BigDecimal quantity,
        AssetType assetType,
        boolean isKimchi
) {}

package dev.asset_tracker_server.api.dto;

import dev.asset_tracker_server.entity.AssetType;

import java.math.BigDecimal;
import java.time.LocalDate;

public record SnapshotSummaryDto(
        String symbol,
        AssetType assetType,
        BigDecimal totalValueUsd,
        BigDecimal totalValueKrw,
        LocalDate snapshotDate
) {}

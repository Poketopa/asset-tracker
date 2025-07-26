package dev.asset_tracker_server.api.dto;

import java.math.BigDecimal;
import java.time.Instant;

public record TickerPriceDto(
        String symbol,
        String assetType,
        BigDecimal price,
        String currency,
        long timestamp
) {}

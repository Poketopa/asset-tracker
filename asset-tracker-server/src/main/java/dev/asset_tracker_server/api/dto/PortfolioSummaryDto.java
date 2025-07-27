package dev.asset_tracker_server.api.dto;

import java.math.BigDecimal;

public record PortfolioSummaryDto(
        BigDecimal totalValueUsd,
        BigDecimal totalValueKrw
) {}

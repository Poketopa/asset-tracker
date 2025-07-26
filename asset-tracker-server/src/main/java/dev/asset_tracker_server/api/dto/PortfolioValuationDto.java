package dev.asset_tracker_server.api.dto;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

@Data
@Builder
public class PortfolioValuationDto {
    private UUID userId;
    private BigDecimal totalValueUsd;
    private BigDecimal totalValueKrw;
    private Instant asOf;
}

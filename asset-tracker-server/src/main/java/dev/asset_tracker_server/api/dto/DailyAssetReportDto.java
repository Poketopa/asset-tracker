package dev.asset_tracker_server.api.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

public record DailyAssetReportDto(
        LocalDate date,
        BigDecimal totalKrw,
        BigDecimal totalUsd
) {}

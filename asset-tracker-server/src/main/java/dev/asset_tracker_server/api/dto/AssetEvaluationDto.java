package dev.asset_tracker_server.api.dto;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Builder
public class AssetEvaluationDto {
    private String symbol;
    private BigDecimal quantity;

    private BigDecimal unitPriceUsd;
    private BigDecimal unitPriceKrw;

    private BigDecimal totalValueUsd;
    private BigDecimal totalValueKrw;

    private String currency;   // USD or USDT or KRW
}

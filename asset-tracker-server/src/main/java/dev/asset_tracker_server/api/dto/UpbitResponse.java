package dev.asset_tracker_server.api.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class UpbitResponse {
    private String market;

    @JsonProperty("trade_price")
    private BigDecimal tradePrice;
}

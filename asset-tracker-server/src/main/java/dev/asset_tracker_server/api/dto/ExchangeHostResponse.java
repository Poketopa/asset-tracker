package dev.asset_tracker_server.api.dto;

import lombok.Data;

import java.math.BigDecimal;
import java.util.Map;

@Data
public class ExchangeHostResponse {
    private Map<String, BigDecimal> rates;
}

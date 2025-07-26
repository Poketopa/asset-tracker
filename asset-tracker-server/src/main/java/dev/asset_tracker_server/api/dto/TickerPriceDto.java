package dev.asset_tracker_server.api.dto;

import java.math.BigDecimal;
import java.time.Instant;

public record TickerPriceDto(
        String ticker,         // ex: BTC/USDT
        String exchange,       // ex: binance, upbit
        BigDecimal price,      // 실시간 가격
        String currency,       // ex: USDT, KRW, USD
        Instant timestamp      // ✅ 수집 시점 기준
) {}

package dev.asset_tracker_server.fetcher;

import dev.asset_tracker_server.api.dto.TickerPriceDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class PriceFetchManager {

    private final List<PriceFetcher> fetchers;

    /**
     * 주어진 거래소에 맞는 fetcher를 찾아서 가격 조회
     *
     * @param exchange 거래소 이름 (예: binance, upbit, finnhub 등)
     * @param symbol API에 전달할 심볼
     * @return 가격 정보 DTO
     */
    public TickerPriceDto fetch(String exchange, String symbol) {
        return fetchers.stream()
                .filter(f -> f.supports(exchange))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("지원하지 않는 거래소: " + exchange))
                .fetchPrice(symbol);
    }
}

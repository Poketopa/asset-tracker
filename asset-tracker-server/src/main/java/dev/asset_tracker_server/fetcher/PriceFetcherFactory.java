package dev.asset_tracker_server.fetcher;

import dev.asset_tracker_server.api.dto.TickerPriceDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class PriceFetcherFactory {

    private final List<PriceFetcher> fetchers;

    public TickerPriceDto getPrice(String exchange, String symbol) {
        return fetchers.stream()
                .filter(f -> f.supports(exchange))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("지원하지 않는 거래소: " + exchange))
                .fetchPrice(symbol);
    }
}

package dev.asset_tracker_server.fetcher;

import dev.asset_tracker_server.api.dto.TickerPriceDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class PriceFetchManager {

    private final List<PriceFetcher> fetchers;

    // 우선순위: binance → bybit → okx → gateio
    private final List<String> priorityExchanges = List.of("binance", "bybit", "okx", "gateio");

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

    /**
     * 우선순위 기반으로 거래소를 순차적으로 조회하여 가격 정보를 가져옵니다.
     * binance → bybit → okx → gateio 순서로 시도합니다.
     *
     * @param symbol 조회할 심볼 (예: BTCUSDT, ETHUSDT, SOLUSDT)
     * @return 가격 정보 DTO
     * @throws RuntimeException 모든 거래소에서 심볼을 찾을 수 없는 경우
     */
    public TickerPriceDto fetchWithPriority(String symbol) {
        for (String exchange : priorityExchanges) {
            try {
                PriceFetcher fetcher = fetchers.stream()
                        .filter(f -> f.supports(exchange))
                        .findFirst()
                        .orElse(null);

                if (fetcher != null) {
                    TickerPriceDto result = fetcher.fetchPrice(symbol);
                    log.info("✅ {} 심볼을 {} 거래소에서 조회 성공: {} {}", 
                            symbol, exchange, result.price(), result.currency());
                    return result;
                }
            } catch (Exception e) {
                log.warn("⚠️ {} 거래소에서 {} 심볼 조회 실패: {}", exchange, symbol, e.getMessage());
                continue; // 다음 거래소로 진행
            }
        }

        log.error("❌ 모든 거래소에서 {} 심볼을 찾을 수 없음", symbol);
        throw new RuntimeException("모든 거래소에서 심볼을 찾을 수 없음: " + symbol);
    }

    /**
     * 특정 거래소에서 심볼이 존재하는지 확인합니다.
     *
     * @param exchange 거래소 이름
     * @param symbol 확인할 심볼
     * @return 존재 여부
     */
    public boolean isSymbolAvailable(String exchange, String symbol) {
        try {
            PriceFetcher fetcher = fetchers.stream()
                    .filter(f -> f.supports(exchange))
                    .findFirst()
                    .orElse(null);

            if (fetcher != null) {
                fetcher.fetchPrice(symbol);
                return true;
            }
        } catch (Exception e) {
            // 심볼이 존재하지 않거나 에러가 발생한 경우
        }
        return false;
    }
}

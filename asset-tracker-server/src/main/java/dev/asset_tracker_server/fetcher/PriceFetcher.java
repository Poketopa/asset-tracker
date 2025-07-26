package dev.asset_tracker_server.fetcher;

import dev.asset_tracker_server.api.dto.TickerPriceDto;

public interface PriceFetcher {

    /**
     * 이 fetcher가 해당 거래소(ex: binance, upbit)를 지원하는지 여부를 반환합니다.
     */
    boolean supports(String exchange);

    /**
     * 주어진 심볼에 대한 가격을 조회합니다.
     * 예: "BTCUSDT", "KRW-BTC", "TSLA"
     *
     * @param symbol API에 사용할 심볼
     * @return 현재 가격 정보 DTO
     */
    TickerPriceDto fetchPrice(String symbol);
}

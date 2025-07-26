package dev.asset_tracker_server.fetcher;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import dev.asset_tracker_server.api.dto.TickerPriceDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.time.Instant;

@Component
@RequiredArgsConstructor
public class UpbitPriceFetcher implements PriceFetcher {

    private final ObjectMapper objectMapper = new ObjectMapper();
    private final RestTemplate restTemplate = new RestTemplate();

    @Override
    public boolean supports(String exchange) {
        return "upbit".equalsIgnoreCase(exchange);
    }

    @Override
    public TickerPriceDto fetchPrice(String symbol) {
        String url = "https://api.upbit.com/v1/ticker?markets=" + symbol;

        try {
            String response = restTemplate.getForObject(url, String.class);
            JsonNode json = objectMapper.readTree(response).get(0); // Upbit은 배열로 응답함

            BigDecimal price = new BigDecimal(json.get("trade_price").asText());

            return new TickerPriceDto(
                    symbol,                 // ex: KRW-BTC
                    "upbit",                // exchange
                    price,                  // 실시간 가격
                    "KRW",                  // Upbit은 항상 KRW 기준
                    Instant.now()          // 수집 시점
            );

        } catch (Exception e) {
            throw new RuntimeException("Upbit 가격 조회 실패 (" + symbol + "): " + e.getMessage(), e);
        }
    }
}

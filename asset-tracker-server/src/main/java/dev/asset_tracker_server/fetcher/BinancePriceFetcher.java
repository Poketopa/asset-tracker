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
public class BinancePriceFetcher implements PriceFetcher {

    private final ObjectMapper objectMapper = new ObjectMapper();
    private final RestTemplate restTemplate = new RestTemplate();

    @Override
    public boolean supports(String exchange) {
        return "binance".equalsIgnoreCase(exchange);
    }

    @Override
    public TickerPriceDto fetchPrice(String symbol) {
        String url = "https://api.binance.com/api/v3/ticker/price?symbol=" + symbol;

        try {
            String response = restTemplate.getForObject(url, String.class);
            JsonNode json = objectMapper.readTree(response);

            BigDecimal price = new BigDecimal(json.get("price").asText());

            return new TickerPriceDto(
                    symbol,                 // ex: BTCUSDT
                    "binance",
                    price,
                    "USDT",                 // Binance 기준 통화
                    Instant.now()
            );

        } catch (Exception e) {
            throw new RuntimeException("Binance 가격 조회 실패 (" + symbol + "): " + e.getMessage(), e);
        }
    }
}

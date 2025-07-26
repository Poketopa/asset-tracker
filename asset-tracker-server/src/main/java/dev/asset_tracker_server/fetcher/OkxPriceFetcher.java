package dev.asset_tracker_server.fetcher;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import dev.asset_tracker_server.api.dto.TickerPriceDto;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.time.Instant;

@Component
public class OkxPriceFetcher implements PriceFetcher {

    private final RestTemplate restTemplate = new RestTemplate();
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public boolean supports(String exchange) {
        return "okx".equalsIgnoreCase(exchange);
    }

    @Override
    public TickerPriceDto fetchPrice(String symbol) {
        String url = "https://www.okx.com/api/v5/market/ticker?instId=" + symbol;
        try {
            String resp = restTemplate.getForObject(url, String.class);
            JsonNode json = objectMapper.readTree(resp);
            JsonNode item = json.get("data").get(0);
            BigDecimal price = new BigDecimal(item.get("last").asText());

            return new TickerPriceDto(
                    symbol,
                    "okx",
                    price,
                    "USDT",
                    Instant.now().toEpochMilli()
            );
        } catch (Exception e) {
            throw new RuntimeException("OKX 가격 조회 실패 (" + symbol + "): " + e.getMessage(), e);
        }
    }
}

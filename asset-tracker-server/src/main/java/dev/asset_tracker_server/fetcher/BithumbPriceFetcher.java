package dev.asset_tracker_server.fetcher;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import dev.asset_tracker_server.api.dto.TickerPriceDto;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.time.Instant;

@Component
public class BithumbPriceFetcher implements PriceFetcher {

    private final RestTemplate restTemplate = new RestTemplate();
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public boolean supports(String exchange) {
        return "bithumb".equalsIgnoreCase(exchange);
    }

    @Override
    public TickerPriceDto fetchPrice(String symbol) {
        // symbol: BTC, ETH 등
        String url = "https://api.bithumb.com/public/ticker/" + symbol + "_KRW";

        try {
            String response = restTemplate.getForObject(url, String.class);
            JsonNode root = objectMapper.readTree(response);
            JsonNode data = root.get("data");
            BigDecimal price = new BigDecimal(data.get("closing_price").asText());

            return new TickerPriceDto(
                    symbol + "/KRW",
                    "bithumb",
                    price,
                    "KRW",
                    Instant.now().toEpochMilli()
            );
        } catch (Exception e) {
            throw new RuntimeException("Bithumb 가격 조회 실패 (" + symbol + "): " + e.getMessage(), e);
        }
    }
}

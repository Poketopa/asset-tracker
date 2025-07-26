package dev.asset_tracker_server.fetcher;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import dev.asset_tracker_server.api.dto.TickerPriceDto;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.time.Instant;

@Component
public class GateioPriceFetcher implements PriceFetcher {

    private final RestTemplate restTemplate = new RestTemplate();
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public boolean supports(String exchange) {
        return "gateio".equalsIgnoreCase(exchange);
    }

    @Override
    public TickerPriceDto fetchPrice(String symbol) {
        String url = "https://api.gateio.ws/api/v4/spot/tickers?currency_pair=" + symbol;
        try {
            String resp = restTemplate.getForObject(url, String.class);
            JsonNode json = objectMapper.readTree(resp).get(0);
            BigDecimal price = new BigDecimal(json.get("last").asText());

            return new TickerPriceDto(
                    symbol,
                    "gateio",
                    price,
                    "USDT",
                    Instant.now()
            );
        } catch (Exception e) {
            throw new RuntimeException("Gate.io 가격 조회 실패 (" + symbol + "): " + e.getMessage(), e);
        }
    }
}

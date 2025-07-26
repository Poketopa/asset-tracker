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
public class FinnhubPriceFetcher implements PriceFetcher {

    private final RestTemplate restTemplate = new RestTemplate();
    private final ObjectMapper objectMapper = new ObjectMapper();
    private final FinnhubProperties finnhubProperties;

    @Override
    public boolean supports(String exchange) {
        return "finnhub".equalsIgnoreCase(exchange);
    }

    @Override
    public TickerPriceDto fetchPrice(String symbol) {
        String token = finnhubProperties.getToken();
        String url = "https://finnhub.io/api/v1/quote?symbol=" + symbol + "&token=" + token;

        try {
            String response = restTemplate.getForObject(url, String.class);
            JsonNode json = objectMapper.readTree(response);

            BigDecimal price = new BigDecimal(json.get("c").asText());

            return new TickerPriceDto(
                    symbol,
                    "finnhub",
                    price,
                    "USD",
                    Instant.now().toEpochMilli() // 또는 Instant.ofEpochSecond(json.get("t").asLong())
            );

        } catch (Exception e) {
            throw new RuntimeException("Finnhub 가격 조회 실패 (" + symbol + "): " + e.getMessage(), e);
        }
    }
}

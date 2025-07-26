package dev.asset_tracker_server.api;

import dev.asset_tracker_server.api.dto.ExchangeHostResponse;
import dev.asset_tracker_server.api.dto.UpbitResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;

@Service
@RequiredArgsConstructor
@Slf4j
public class ExchangeRateApiClient {

    private final RestTemplate restTemplate = new RestTemplate();

    private static final String EXCHANGE_RATE_API = "https://api.exchangerate.host/latest?base=USD&symbols=KRW";
    private static final String UPBIT_USDT_KRW_API = "https://api.upbit.com/v1/ticker?markets=KRW-USDT";

    public BigDecimal fetchUsdToKrw() {
        try {
            ResponseEntity<ExchangeHostResponse> response = restTemplate.getForEntity(EXCHANGE_RATE_API, ExchangeHostResponse.class);
            BigDecimal rate = response.getBody().getRates().get("KRW");
            log.info("[환율] USD→KRW = {}", rate);
            return rate;
        } catch (Exception e) {
            log.error("USD 환율 조회 실패", e);
            throw new RuntimeException("USD 환율 조회 실패");
        }
    }

    public BigDecimal fetchUsdtToKrw() {
        try {
            ResponseEntity<UpbitResponse[]> response = restTemplate.getForEntity(UPBIT_USDT_KRW_API, UpbitResponse[].class);
            UpbitResponse[] data = response.getBody();

            if (data == null || data.length == 0) {
                throw new RuntimeException("업비트 응답이 비어 있습니다.");
            }

            BigDecimal price = data[0].getTradePrice();
            log.info("[환율] USDT→KRW = {}", price);
            return price;
        } catch (Exception e) {
            log.error("USDT 환율 조회 실패", e);
            throw new RuntimeException("USDT 환율 조회 실패");
        }
    }
}

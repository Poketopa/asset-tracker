package dev.asset_tracker_server.test;

import dev.asset_tracker_server.api.ExchangeRateApiClient;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;

@Tag(name = "ExchangeTest", description = "환율 테스트용 API")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/test")
public class ExchangeTestController {

    private final ExchangeRateApiClient apiClient;

    @Operation(summary = "USD→KRW 환율 조회", description = "ExchangeRateHost API를 통해 USD→KRW 환율을 조회합니다.")
    @GetMapping("/usd-krw")
    public BigDecimal usdKrw() {
        return apiClient.fetchUsdToKrw();
    }

    @Operation(summary = "USDT→KRW 환율 조회", description = "Upbit API를 통해 USDT→KRW 환율을 조회합니다.")
    @GetMapping("/usdt-krw")
    public BigDecimal usdtKrw() {
        return apiClient.fetchUsdtToKrw();
    }
}

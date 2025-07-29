package dev.asset_tracker_server.controller;

import dev.asset_tracker_server.entity.ExchangeRate;
import dev.asset_tracker_server.repository.ExchangeRateRepository;
import dev.asset_tracker_server.service.ExchangeRateFetchService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@Tag(name = "ExchangeRate", description = "환율 관리 API")
@RestController
@RequestMapping("/api/exchange-rate")
@RequiredArgsConstructor
@Slf4j
public class ExchangeRateController {
    private final ExchangeRateFetchService exchangeRateFetchService;
    private final ExchangeRateRepository exchangeRateRepository;

    @Operation(summary = "환율 수집 및 저장", description = "USD/KRW와 USDT/KRW 환율을 수집하여 DB에 저장합니다.")
    @PostMapping("/fetch-and-save")
    public ResponseEntity<Map<String, Object>> fetchAndSaveExchangeRate() {
        try {
            exchangeRateFetchService.fetchAndSaveExchangeRates();
            return ResponseEntity.ok(Map.of("success", true, "message", "환율 데이터 수집 및 저장 완료"));
        } catch (Exception e) {
            log.error("환율 수집 실패", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("success", false, "error", e.getMessage()));
        }
    }

    @Operation(summary = "최신 환율 조회", description = "USD/KRW와 USDT/KRW 최신 환율을 모두 조회합니다.")
    @GetMapping("/latest")
    public ResponseEntity<Map<String, Object>> getLatestRates() {
        try {
            // USD/KRW 환율 조회
            ExchangeRate usdRate = exchangeRateRepository
                    .findLatestByType("USD/KRW", PageRequest.of(0, 1))
                    .stream()
                    .findFirst()
                    .orElseThrow(() -> new RuntimeException("USD/KRW 환율 정보 없음"));

            // USDT/KRW 환율 조회
            ExchangeRate usdtRate = exchangeRateRepository
                    .findLatestByType("USDT/KRW", PageRequest.of(0, 1))
                    .stream()
                    .findFirst()
                    .orElseThrow(() -> new RuntimeException("USDT/KRW 환율 정보 없음"));

            Map<String, Object> response = Map.of(
                    "usdKrw", usdRate.getUsdToKrw(),
                    "usdtKrw", usdtRate.getUsdtToKrw(),
                    "timestamp", usdRate.getTimestamp()
            );

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("환율 조회 실패: {}", e.getMessage());
            return ResponseEntity.notFound().build();
        }
    }
}

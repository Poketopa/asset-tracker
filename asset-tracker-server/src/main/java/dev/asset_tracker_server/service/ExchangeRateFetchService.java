package dev.asset_tracker_server.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import dev.asset_tracker_server.api.ExchangeRateApiClient;
import dev.asset_tracker_server.entity.ExchangeRate;
import dev.asset_tracker_server.repository.ExchangeRateRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Slf4j
public class ExchangeRateFetchService {
    private final ExchangeRateRepository exchangeRateRepository;
    private final ExchangeRateApiClient exchangeRateApiClient;
    private final ObjectMapper objectMapper = new ObjectMapper();

    public BigDecimal fetchUsdKrwFromPython() {
        try {
            ProcessBuilder processBuilder = new ProcessBuilder(
                    "python3", "script/exchange_rate_fetcher.py"  // ✅ 올바른 경로
            );
            processBuilder.redirectErrorStream(true);
            Process process = processBuilder.start();
            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            StringBuilder output = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) output.append(line);
            int exitCode = process.waitFor();
            if (exitCode == 0) {
                JsonNode jsonNode = objectMapper.readTree(output.toString());
                if (jsonNode.get("success").asBoolean()) {
                    BigDecimal rate = new BigDecimal(jsonNode.get("rate").asText());
                    log.info("✅ USD/KRW 환율 수집 성공 (Python): {}", rate);
                    return rate;
                } else {
                    log.error("❌ 파이썬 스크립트 실행 실패: {}", jsonNode.get("error").asText());
                    return null;
                }
            } else {
                log.error("❌ 파이썬 스크립트 실행 실패 (exit code: {})", exitCode);
                return null;
            }
        } catch (Exception e) {
            log.error("❌ USD/KRW 환율 수집 중 오류 발생", e);
            return null;
        }
    }

    public BigDecimal fetchUsdtKrwFromUpbit() {
        try {
            BigDecimal rate = exchangeRateApiClient.fetchUsdtToKrw();
            log.info("✅ USDT/KRW 환율 수집 성공 (Upbit): {}", rate);
            return rate;
        } catch (Exception e) {
            log.error("❌ USDT/KRW 환율 수집 실패", e);
            return null;
        }
    }

    public void fetchAndSaveExchangeRates() {
        log.info("🔄 환율 수집 및 저장 시작");
        BigDecimal usdKrw = fetchUsdKrwFromPython();
        BigDecimal usdtKrw = fetchUsdtKrwFromUpbit();
        if (usdKrw != null) saveExchangeRate("USD/KRW", usdKrw, BigDecimal.ZERO);
        if (usdtKrw != null) saveExchangeRate("USDT/KRW", BigDecimal.ZERO, usdtKrw);
        log.info("✅ 환율 수집 및 저장 완료");
    }

    private void saveExchangeRate(String type, BigDecimal usdToKrw, BigDecimal usdtToKrw) {
        ExchangeRate exchangeRate = ExchangeRate.builder()
                .type(type)
                .usdToKrw(usdToKrw)
                .usdtToKrw(usdtToKrw)
                .timestamp(LocalDateTime.now())
                .build();
        exchangeRateRepository.save(exchangeRate);
        log.info("💾 환율 저장 완료: {} (USD/KRW: {}, USDT/KRW: {})", type, usdToKrw, usdtToKrw);
    }
}

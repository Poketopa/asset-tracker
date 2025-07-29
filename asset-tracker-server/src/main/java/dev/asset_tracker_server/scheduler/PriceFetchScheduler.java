package dev.asset_tracker_server.scheduler;

import dev.asset_tracker_server.api.dto.TickerPriceDto;
import dev.asset_tracker_server.fetcher.PriceFetchManager;
import dev.asset_tracker_server.service.AssetPriceHistoryService;
import dev.asset_tracker_server.service.AssetSnapshotService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;

@RequiredArgsConstructor
@Slf4j
@Component
public class PriceFetchScheduler {

    private final PriceFetchManager priceFetchManager;
    private final AssetSnapshotService assetSnapshotService;
    private final AssetPriceHistoryService assetPriceHistoryService;

    // 주요 암호화폐 심볼 리스트 (우선순위 기반으로 조회)
    private final List<String> cryptoSymbols = List.of(
            "BTCUSDT", "ETHUSDT", "SOLUSDT", "ADAUSDT", "DOTUSDT",
            "LINKUSDT", "LTCUSDT", "BCHUSDT", "XRPUSDT", "BNBUSDT",
            "MATICUSDT", "AVAXUSDT", "UNIUSDT", "ATOMUSDT", "NEARUSDT"
    );

    // 주요 주식 심볼 리스트
    private final List<String> stockSymbols = List.of(
            "AAPL", "MSFT", "GOOGL", "AMZN", "TSLA",
            "NVDA", "META", "NFLX", "TSM", "JPM"
    );

    @Scheduled(fixedRate = 60_000)
    public void fetchAllPrices() {
        log.info("🔄 가격 수집 시작");

        // 암호화폐 가격 수집 (우선순위 기반)
        for (String symbol : cryptoSymbols) {
            try {
                TickerPriceDto dto = priceFetchManager.fetchWithPriority(symbol);

                log.info("📈 [암호화폐] {} = {} {} @ {} (거래소: {})",
                        symbol,
                        dto.price(),
                        dto.currency(),
                        dto.timestamp(),
                        dto.exchange()
                );

                // 💾 저장
                assetSnapshotService.savePriceSnapshot(dto);
                assetPriceHistoryService.save(dto);

            } catch (Exception e) {
                log.warn("⚠️ 암호화폐 가격 수집 실패: {} - {}", symbol, e.getMessage());
            }
        }

        // 주식 가격 수집 (finnhub)
        for (String symbol : stockSymbols) {
            try {
                TickerPriceDto dto = priceFetchManager.fetch("finnhub", symbol);

                log.info("📈 [주식] {} = {} {} @ {} (거래소: {})",
                        symbol,
                        dto.price(),
                        dto.currency(),
                        dto.timestamp(),
                        dto.exchange()
                );

                // 💾 저장
                assetSnapshotService.savePriceSnapshot(dto);
                assetPriceHistoryService.save(dto);

            } catch (Exception e) {
                log.warn("⚠️ 주식 가격 수집 실패: {} - {}", symbol, e.getMessage());
            }
        }

        log.info("✅ 가격 수집 완료");
    }
}

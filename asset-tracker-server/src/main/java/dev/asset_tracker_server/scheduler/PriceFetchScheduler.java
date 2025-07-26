package dev.asset_tracker_server.scheduler;

import dev.asset_tracker_server.api.dto.TickerPriceDto;
import dev.asset_tracker_server.entity.SymbolMapping;
import dev.asset_tracker_server.fetcher.PriceFetchManager;
import dev.asset_tracker_server.repository.SymbolMappingRepository;
import dev.asset_tracker_server.service.AssetSnapshotService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class PriceFetchScheduler {

    private final SymbolMappingRepository symbolMappingRepository;
    private final PriceFetchManager priceFetchManager;
    private final AssetSnapshotService assetSnapshotService;

    // 매 1분마다 가격 수집 (테스트 목적, 운영에서는 적절한 주기로 조절)
    @Scheduled(fixedRate = 60_000)
    public void fetchAllPrices() {
        log.info("🔄 가격 수집 시작");

        List<SymbolMapping> symbols = symbolMappingRepository.findByIsActiveTrue();

        for (SymbolMapping mapping : symbols) {
            try {
                TickerPriceDto dto = priceFetchManager.fetch(mapping.getExchange().name(), mapping.getExchangeSymbol());
                log.info("📈 [{}] = {} {} @ {}", mapping.getExchangeSymbol(), dto.price(), dto.currency(), dto.timestamp());

                // 👉 가격 저장 처리
                assetSnapshotService.saveRawSnapshot(dto);

            } catch (Exception e) {
                log.warn("⚠️ 가격 수집 실패: {} - {}", mapping.getExchangeSymbol(), e.getMessage());
            }
        }

        log.info("✅ 가격 수집 완료");
    }
}

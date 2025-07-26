package dev.asset_tracker_server.fetcher;

import dev.asset_tracker_server.api.dto.TickerPriceDto;
import dev.asset_tracker_server.entity.SymbolMapping;
import dev.asset_tracker_server.repository.SymbolMappingRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class PriceFetchScheduler {

    private final SymbolMappingRepository symbolMappingRepository;
    private final PriceFetchManager priceFetchManager;

    @Scheduled(fixedRate = 60_000)
    public void fetchAllPrices() {
        List<SymbolMapping> symbols = symbolMappingRepository.findByIsActiveTrue();

        for (SymbolMapping mapping : symbols) {
            try {
                TickerPriceDto dto = priceFetchManager.fetch(mapping.getExchange().name(), mapping.getSymbol());
                log.info("[{}] {} = {} {} @ {}",
                        mapping.getExchange(),
                        mapping.getSymbol(),
                        dto.price(),
                        dto.currency(),
                        dto.timestamp()
                );

                // 👉 다음 단계에서 저장 처리 예정
                // assetSnapshotService.save(dto);

            } catch (Exception e) {
                log.warn("가격 조회 실패: {} - {} ({})", mapping.getExchange(), mapping.getSymbol(), e.getMessage());
            }
        }
    }
}

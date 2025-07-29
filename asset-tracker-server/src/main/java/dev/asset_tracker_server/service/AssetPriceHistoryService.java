package dev.asset_tracker_server.service;

import dev.asset_tracker_server.api.dto.TickerPriceDto;
import dev.asset_tracker_server.entity.Asset;
import dev.asset_tracker_server.entity.AssetPriceHistory;
import dev.asset_tracker_server.entity.AssetType;
import dev.asset_tracker_server.entity.ExchangeRate;
import dev.asset_tracker_server.fetcher.PriceFetcherFactory;
import dev.asset_tracker_server.repository.AssetPriceHistoryRepository;
import dev.asset_tracker_server.repository.AssetRepository;
import dev.asset_tracker_server.repository.ExchangeRateRepository;
import lombok.Builder;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
@Builder
public class AssetPriceHistoryService {

    private final AssetPriceHistoryRepository historyRepository;
    private final ExchangeRateRepository exchangeRateRepository;
    private final AssetRepository assetRepository;
    private final PriceFetcherFactory priceFetcherFactory;

    public void save(TickerPriceDto dto) {
        String rateType = switch (dto.currency().toUpperCase()) {
            case "USDT" -> "USDT/KRW";
            case "USD" -> "USD/KRW";
            default -> throw new IllegalArgumentException("지원하지 않는 통화: " + dto.currency());
        };

        ExchangeRate latestRate = exchangeRateRepository.findLatestByType(rateType, PageRequest.of(0, 1))
                .stream()
                .findFirst()
                .orElseThrow(() -> new RuntimeException("환율 정보 없음: " + rateType));

        BigDecimal priceUsd = dto.price();
        BigDecimal priceKrw = priceUsd.multiply(latestRate.getRate());

        AssetPriceHistory history = new AssetPriceHistory();
        history.setSymbol(dto.symbol());
        history.setTimestamp(LocalDateTime.ofInstant(Instant.ofEpochMilli(dto.timestamp()), java.time.ZoneId.systemDefault()));
        history.setPriceUsd(priceUsd.toPlainString());
        history.setPriceKrw(priceKrw.toPlainString());

        historyRepository.save(history);
        log.info("📝 가격 히스토리 저장 완료: {} | {} USD ≒ {} KRW", dto.symbol(), priceUsd, priceKrw);
    }

    public List<AssetPriceHistory> getRecentHistory(String symbol, int limit) {
        return historyRepository.findBySymbolAndTimestampBetween(symbol, LocalDateTime.now().minusDays(limit), LocalDateTime.now());
    }
}

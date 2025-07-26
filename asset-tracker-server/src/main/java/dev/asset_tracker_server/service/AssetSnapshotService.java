package dev.asset_tracker_server.service;

import dev.asset_tracker_server.api.dto.SnapshotHistoryDto;
import dev.asset_tracker_server.api.dto.SnapshotSummaryDto;
import dev.asset_tracker_server.api.dto.TickerPriceDto;
import dev.asset_tracker_server.entity.AssetSnapshot;
import dev.asset_tracker_server.entity.ExchangeRate;
import dev.asset_tracker_server.repository.AssetSnapshotRepository;
import dev.asset_tracker_server.repository.ExchangeRateRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;
import dev.asset_tracker_server.entity.AssetType;
import dev.asset_tracker_server.entity.User;

@Service
@RequiredArgsConstructor
@Slf4j
public class AssetSnapshotService {

    private final AssetSnapshotRepository assetSnapshotRepository;
    private final ExchangeRateRepository exchangeRateRepository;

    // 시스템 계정 UUID (개발용, 실무에서는 별도 계정 관리 권장)
    private static final UUID SYSTEM_USER_ID = UUID.fromString("00000000-0000-0000-0000-000000000000");

    public void saveRawSnapshot(TickerPriceDto dto) {
        AssetSnapshot snapshot = AssetSnapshot.builder()
                .user(User.builder().id(SYSTEM_USER_ID).build())
                .symbol(dto.symbol())
                .assetType(AssetType.valueOf(dto.assetType()))
                .totalValueUsd(dto.price())
                .totalValueKrw(null)  // 환산 없음
                .snapshotDate(LocalDate.now())
                .build();

        assetSnapshotRepository.save(snapshot);
        log.info("💾 자산(RAW) 저장 완료: {} ({} {} @ {})", dto.symbol(), dto.price(), dto.currency(), dto.timestamp());
    }

    public void saveConvertedSnapshotWithKrw(TickerPriceDto dto) {
        ExchangeRate latest = exchangeRateRepository
                .findLatestByType("USD/KRW", PageRequest.of(0, 1)) // 또는 enum 등
                .stream().findFirst()
                .orElseThrow(() -> new RuntimeException("환율 정보 없음"));

        int usdToKrw = latest.getUsdToKrw().intValue();

        BigDecimal priceUsd = dto.price();
        BigDecimal priceKrw = priceUsd.multiply(BigDecimal.valueOf(usdToKrw));

        AssetSnapshot snapshot = AssetSnapshot.builder()
                .user(User.builder().id(SYSTEM_USER_ID).build())
                .symbol(dto.symbol())
                .assetType(AssetType.valueOf(dto.assetType()))
                .totalValueUsd(priceUsd)
                .totalValueKrw(priceKrw)
                .snapshotDate(LocalDate.now())
                .build();

        assetSnapshotRepository.save(snapshot);
        log.info("💾 자산(KRW 변환) 저장 완료: {} ({} KRW)", dto.symbol(), priceKrw);
    }

    public void savePriceSnapshot(TickerPriceDto dto) {
        String rateType = switch (dto.currency()) {
            case "USDT" -> "USDT/KRW";
            case "USD" -> "USD/KRW";
            default -> throw new IllegalArgumentException("지원하지 않는 환산 통화: " + dto.currency());
        };

        ExchangeRate latest = exchangeRateRepository.findLatestByType(rateType, PageRequest.of(0, 1))
                .stream()
                .findFirst()
                .orElseThrow(() -> new RuntimeException("환율 정보 없음: " + rateType));

        BigDecimal priceKrw = dto.price().multiply(latest.getRate());

        AssetSnapshot snapshot = AssetSnapshot.builder()
                .user(User.builder().id(SYSTEM_USER_ID).build())
                .symbol(dto.symbol())
                .assetType(AssetType.valueOf(dto.assetType()))
                .totalValueUsd(dto.price())
                .totalValueKrw(priceKrw)
                .snapshotDate(LocalDate.now())
                .build();

        assetSnapshotRepository.save(snapshot);
        log.info("💾 자산 저장 완료: {} ({} {} ≒ {} KRW)", dto.symbol(), dto.price(), dto.currency(), priceKrw);
    }

    public void saveSnapshot(TickerPriceDto dto) {
        String currency = dto.currency().toUpperCase();

        if (currency.equals("USD") || currency.equals("USDT")) {
            saveConvertedSnapshotWithKrw(dto);
        } else {
            saveRawSnapshot(dto);
        }
    }

    public List<SnapshotSummaryDto> getTodaySnapshotsByUser(UUID userId) {
        User user = User.builder().id(userId).build();
        LocalDate today = LocalDate.now();

        return assetSnapshotRepository.findByUserAndSnapshotDate(user, today)
                .stream()
                .map(snapshot -> new SnapshotSummaryDto(
                        snapshot.getSymbol(),
                        snapshot.getAssetType(),
                        snapshot.getTotalValueUsd(),
                        snapshot.getTotalValueKrw(),
                        snapshot.getSnapshotDate()
                ))
                .toList();
    }

    public List<SnapshotHistoryDto> getSnapshotHistoryBySymbol(UUID userId, String symbol) {
        User user = User.builder().id(userId).build();

        return assetSnapshotRepository.findByUserAndSymbolOrderBySnapshotDateAsc(user, symbol)
                .stream()
                .map(snapshot -> new SnapshotHistoryDto(
                        snapshot.getTotalValueUsd(),
                        snapshot.getTotalValueKrw(),
                        snapshot.getSnapshotDate()
                ))
                .toList();
    }
}

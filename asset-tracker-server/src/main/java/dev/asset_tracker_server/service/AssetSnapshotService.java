package dev.asset_tracker_server.service;

import dev.asset_tracker_server.api.dto.SnapshotHistoryDto;
import dev.asset_tracker_server.api.dto.SnapshotSummaryDto;
import dev.asset_tracker_server.api.dto.TickerPriceDto;
import dev.asset_tracker_server.entity.AssetSnapshot;
import dev.asset_tracker_server.entity.ExchangeRate;
import dev.asset_tracker_server.repository.AssetSnapshotRepository;
import dev.asset_tracker_server.repository.ExchangeRateRepository;
import dev.asset_tracker_server.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import dev.asset_tracker_server.entity.AssetType;
import dev.asset_tracker_server.entity.User;

@Service
@RequiredArgsConstructor
@Slf4j
public class AssetSnapshotService {

    private final AssetSnapshotRepository assetSnapshotRepository;
    private final ExchangeRateRepository exchangeRateRepository;
    private final UserRepository userRepository;

    // 시스템 계정 ID (개발용, 실무에서는 별도 계정 관리 권장)
    private static final Long SYSTEM_USER_ID = 1L;

    /**
     * 심볼을 기반으로 AssetType을 추론하는 메서드
     */
    private AssetType inferAssetType(String symbol) {
        if (symbol.endsWith("USDT") || symbol.endsWith("BTC") || symbol.endsWith("ETH")) {
            return AssetType.CRYPTO;
        } else if (symbol.matches("[A-Z]{1,5}")) { // 주식 심볼 패턴 (1-5자리 대문자)
            return AssetType.STOCK;
        } else {
            return AssetType.CRYPTO; // 기본값
        }
    }

    public void saveRawSnapshot(TickerPriceDto dto) {
        AssetSnapshot snapshot = AssetSnapshot.builder()
                .user(User.builder().id(SYSTEM_USER_ID).build())
                .symbol(dto.symbol())
                .assetType(inferAssetType(dto.symbol()))
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
                .assetType(inferAssetType(dto.symbol()))
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
                .assetType(inferAssetType(dto.symbol()))
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

    public List<SnapshotSummaryDto> getTodaySnapshotsByUser(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("사용자 없음"));

        LocalDate today = LocalDate.now();
        List<AssetSnapshot> snapshots = assetSnapshotRepository.findByUserAndSnapshotDate(user, today);

        return snapshots.stream()
                .map(snapshot -> new SnapshotSummaryDto(
                        snapshot.getSymbol(),
                        snapshot.getAssetType(),
                        snapshot.getTotalValueUsd(),
                        snapshot.getTotalValueKrw(),
                        snapshot.getSnapshotDate()
                ))
                .toList();
    }

    public List<SnapshotHistoryDto> getSnapshotHistoryBySymbol(Long userId, String symbol) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("사용자 없음"));

        List<AssetSnapshot> snapshots = assetSnapshotRepository.findByUserAndSymbolOrderBySnapshotDateDesc(user, symbol);

        return snapshots.stream()
                .map(snapshot -> new SnapshotHistoryDto(
                        snapshot.getTotalValueUsd(),
                        snapshot.getTotalValueKrw(),
                        snapshot.getSnapshotDate()
                ))
                .toList();
    }

    public List<AssetSnapshot> getSnapshots(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("사용자 없음"));
        return assetSnapshotRepository.findByUser(user);
    }

    public List<AssetSnapshot> getSnapshotsByDate(Long userId, LocalDate date) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("사용자 없음"));
        return assetSnapshotRepository.findByUserAndSnapshotDate(user, date);
    }
}

package dev.asset_tracker_server.service;

import dev.asset_tracker_server.api.dto.SnapshotChartDto;
import dev.asset_tracker_server.entity.AssetSnapshot;
import dev.asset_tracker_server.entity.User;
import dev.asset_tracker_server.repository.AssetSnapshotRepository;
import dev.asset_tracker_server.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AssetChartService {

    private final AssetSnapshotRepository assetSnapshotRepository;
    private final UserRepository userRepository;

    public List<SnapshotChartDto> getDailySnapshots(UUID userId, String currency) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("유저 없음"));

        List<AssetSnapshot> snapshots = assetSnapshotRepository.findByUserOrderBySnapshotDateAsc(user);

        // 날짜별로 그룹핑해서 합산
        Map<LocalDate, BigDecimal> grouped = new TreeMap<>();

        for (AssetSnapshot s : snapshots) {
            BigDecimal value = "KRW".equalsIgnoreCase(currency) ? s.getTotalValueKrw() : s.getTotalValueUsd();
            grouped.merge(s.getSnapshotDate(), value, BigDecimal::add);
        }

        return grouped.entrySet().stream()
                .map(e -> new SnapshotChartDto(e.getKey(), e.getValue()))
                .toList();
    }
}

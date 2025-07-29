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

@Service
@RequiredArgsConstructor
public class AssetChartService {

    private final AssetSnapshotRepository assetSnapshotRepository;
    private final UserRepository userRepository;

    public List<SnapshotChartDto> getDailySnapshots(Long userId, String currency) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("사용자 없음"));

        List<AssetSnapshot> snapshots = assetSnapshotRepository.findByUserOrderBySnapshotDateAsc(user);

        return snapshots.stream()
                .map(snapshot -> new SnapshotChartDto(
                        snapshot.getSnapshotDate(),
                        currency.equals("USD") ? snapshot.getTotalValueUsd() : snapshot.getTotalValueKrw()
                ))
                .toList();
    }
}

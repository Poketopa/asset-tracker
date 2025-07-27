package dev.asset_tracker_server.controller;

import dev.asset_tracker_server.api.dto.SnapshotChartDto;
import dev.asset_tracker_server.service.AssetChartService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/chart")
public class AssetChartController {

    private final AssetChartService assetChartService;

    @GetMapping("/{userId}")
    public ResponseEntity<List<SnapshotChartDto>> getChartData(
            @PathVariable UUID userId,
            @RequestParam(defaultValue = "KRW") String currency
    ) {
        List<SnapshotChartDto> chartData = assetChartService.getDailySnapshots(userId, currency);
        return ResponseEntity.ok(chartData);
    }
}

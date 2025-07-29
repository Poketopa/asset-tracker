package dev.asset_tracker_server.controller;

import dev.asset_tracker_server.api.dto.SnapshotHistoryDto;
import dev.asset_tracker_server.api.dto.SnapshotSummaryDto;
import dev.asset_tracker_server.service.AssetSnapshotService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/snapshot")
@RequiredArgsConstructor
public class SnapshotController {

    private final AssetSnapshotService assetSnapshotService;

    @GetMapping("/{userId}/latest")
    public List<SnapshotSummaryDto> getLatestSnapshots(@PathVariable Long userId) {
        return assetSnapshotService.getTodaySnapshotsByUser(userId);
    }

    @GetMapping("/{userId}/history/{symbol}")
    public List<SnapshotHistoryDto> getSnapshotHistory(
            @PathVariable Long userId,
            @PathVariable String symbol
    ) {
        return assetSnapshotService.getSnapshotHistoryBySymbol(userId, symbol);
    }
}

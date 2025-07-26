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
import java.util.UUID;

@RestController
@RequestMapping("/api/snapshot")
@RequiredArgsConstructor
public class SnapshotController {

    private final AssetSnapshotService assetSnapshotService;

    @GetMapping("/{userId}/latest")
    public List<SnapshotSummaryDto> getLatestSnapshots(@PathVariable UUID userId) {
        return assetSnapshotService.getTodaySnapshotsByUser(userId);
    }

    @GetMapping("/{userId}/{symbol}/history")
    public List<SnapshotHistoryDto> getHistory(
            @PathVariable UUID userId,
            @PathVariable String symbol
    ) {
        return assetSnapshotService.getSnapshotHistoryBySymbol(userId, symbol);
    }
}

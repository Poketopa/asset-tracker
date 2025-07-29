package dev.asset_tracker_server.controller;

import dev.asset_tracker_server.entity.AssetSnapshot;
import dev.asset_tracker_server.service.AssetSnapshotService;
import dev.asset_tracker_server.api.dto.SnapshotSummaryDto;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/snapshots")
@RequiredArgsConstructor
public class AssetSnapshotController {

    private final AssetSnapshotService assetSnapshotService;

    @GetMapping("/{userId}")
    public List<AssetSnapshot> getAllSnapshots(@PathVariable Long userId) {
        return assetSnapshotService.getSnapshots(userId);
    }

    @GetMapping("/{userId}/date")
    public ResponseEntity<List<AssetSnapshot>> getSnapshotsByDate(
            @PathVariable Long userId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date
    ) {
        List<AssetSnapshot> snapshots = assetSnapshotService.getSnapshotsByDate(userId, date);
        return ResponseEntity.ok(snapshots);
    }

    @GetMapping("/{userId}/today")
    public List<SnapshotSummaryDto> getTodaySnapshots(@PathVariable Long userId) {
        return assetSnapshotService.getTodaySnapshotsByUser(userId);
    }
}

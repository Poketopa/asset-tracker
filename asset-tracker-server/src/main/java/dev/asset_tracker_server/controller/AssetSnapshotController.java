package dev.asset_tracker_server.controller;

import dev.asset_tracker_server.entity.AssetSnapshot;
import dev.asset_tracker_server.service.AssetSnapshotService;
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
import java.util.UUID;

@RestController
@RequestMapping("/api/snapshots")
@RequiredArgsConstructor
public class AssetSnapshotController {

    private final AssetSnapshotService assetSnapshotService;

    @GetMapping("/{userId}")
    public List<AssetSnapshot> getAllSnapshots(@PathVariable UUID userId) {
        return assetSnapshotService.getSnapshots(userId);
    }

    @GetMapping("/{userId}/date")
    public ResponseEntity<List<AssetSnapshot>> getSnapshotsByDate(
            @PathVariable UUID userId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date
    ) {
        List<AssetSnapshot> snapshots = assetSnapshotService.getSnapshotsByDate(userId, date);
        return ResponseEntity.ok(snapshots);
    }
}

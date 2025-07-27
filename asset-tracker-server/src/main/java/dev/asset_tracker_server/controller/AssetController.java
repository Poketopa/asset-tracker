package dev.asset_tracker_server.controller;

import dev.asset_tracker_server.entity.Asset;
import dev.asset_tracker_server.service.AssetService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/assets")
public class AssetController {

    private final AssetService assetService;

    @GetMapping("/{userId}")
    public List<AssetService.AssetDto> getAssets(@PathVariable UUID userId) {
        return assetService.getAssetsByUser(userId);
    }

    @PostMapping("/{userId}")
    public ResponseEntity<?> registerAsset(
            @PathVariable UUID userId,
            @RequestBody Asset asset
    ) {
        assetService.saveAsset(userId, asset);
        return ResponseEntity.ok("자산 등록 완료");
    }

    @PutMapping("/{userId}/{assetId}")
    public ResponseEntity<?> updateAsset(
            @PathVariable UUID userId,
            @PathVariable UUID assetId,
            @RequestBody Asset updatedAsset
    ) {
        assetService.updateAsset(userId, assetId, updatedAsset);
        return ResponseEntity.ok("자산 수정 완료");
    }

    @DeleteMapping("/{userId}/{assetId}")
    public ResponseEntity<?> deleteAsset(
            @PathVariable UUID userId,
            @PathVariable UUID assetId
    ) {
        assetService.deleteAsset(userId, assetId);
        return ResponseEntity.ok("자산 삭제 완료");
    }
}

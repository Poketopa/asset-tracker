package dev.asset_tracker_server.controller;

import dev.asset_tracker_server.entity.Asset;
import dev.asset_tracker_server.service.AssetService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
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
}

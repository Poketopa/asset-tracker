package dev.asset_tracker_server.controller;

import dev.asset_tracker_server.api.dto.PortfolioValuationDto;
import dev.asset_tracker_server.service.AssetValuationService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/valuation")
public class AssetValuationController {

    private final AssetValuationService assetValuationService;

    @GetMapping("/{userId}")
    public PortfolioValuationDto getValuation(@PathVariable Long userId) {
        return assetValuationService.getPortfolioSummary(userId);
    }
}

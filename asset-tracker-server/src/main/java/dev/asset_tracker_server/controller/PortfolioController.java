package dev.asset_tracker_server.controller;

import dev.asset_tracker_server.api.dto.PortfolioHistoryDto;
import dev.asset_tracker_server.api.dto.PortfolioSummaryDto;
import dev.asset_tracker_server.service.AssetPortfolioService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/portfolio")
@RequiredArgsConstructor
public class PortfolioController {

    private final AssetPortfolioService assetPortfolioService;

    @GetMapping("/{userId}/summary")
    public PortfolioSummaryDto getSummary(@PathVariable UUID userId) {
        return assetPortfolioService.getPortfolioSummary(userId);
    }

    @GetMapping("/{userId}/history")
    public List<PortfolioHistoryDto> getHistory(@PathVariable UUID userId) {
        return assetPortfolioService.getPortfolioHistory(userId);
    }
}

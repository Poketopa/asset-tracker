package dev.asset_tracker_server.controller;

import dev.asset_tracker_server.api.dto.AssetEvaluationDto;
import dev.asset_tracker_server.service.AssetPortfolioService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/portfolio")
public class AssetPortfolioController {

    private final AssetPortfolioService assetPortfolioService;

    @GetMapping("/{userId}/total")
    public ResponseEntity<Map<String, BigDecimal>> getTotalValue(@PathVariable UUID userId) {
        Map<String, BigDecimal> total = assetPortfolioService.calculatePortfolioValue(userId);
        return ResponseEntity.ok(total);
    }

    @GetMapping("/{userId}/evaluate")
    public ResponseEntity<List<AssetEvaluationDto>> getEvaluationDetails(@PathVariable UUID userId) {
        List<AssetEvaluationDto> details = assetPortfolioService.evaluatePortfolio(userId);
        return ResponseEntity.ok(details);
    }
}

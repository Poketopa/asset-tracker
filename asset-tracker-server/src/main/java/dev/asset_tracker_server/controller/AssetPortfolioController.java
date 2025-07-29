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

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/portfolio")
public class AssetPortfolioController {

    private final AssetPortfolioService assetPortfolioService;

    @GetMapping("/{userId}/total-value")
    public ResponseEntity<Map<String, BigDecimal>> getTotalValue(@PathVariable Long userId) {
        Map<String, BigDecimal> totalValue = assetPortfolioService.calculatePortfolioValue(userId);
        return ResponseEntity.ok(totalValue);
    }

    @GetMapping("/{userId}/evaluation")
    public ResponseEntity<List<AssetEvaluationDto>> getEvaluationDetails(@PathVariable Long userId) {
        List<AssetEvaluationDto> evaluations = assetPortfolioService.evaluatePortfolio(userId);
        return ResponseEntity.ok(evaluations);
    }
}

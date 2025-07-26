package dev.asset_tracker_server.controller;

import dev.asset_tracker_server.entity.AssetPriceHistory;
import dev.asset_tracker_server.service.AssetPriceHistoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/history")
@RequiredArgsConstructor
public class PriceHistoryController {

    private final AssetPriceHistoryService historyService;

    @GetMapping("/{symbol}")
    public List<AssetPriceHistory> getPriceHistory(
            @PathVariable String symbol,
            @RequestParam(defaultValue = "30") int limit
    ) {
        return historyService.getRecentHistory(symbol, limit);
    }
}

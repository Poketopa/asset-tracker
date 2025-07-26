package dev.asset_tracker_server.controller;

import dev.asset_tracker_server.api.dto.DailyAssetReportDto;
import dev.asset_tracker_server.service.ReportService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/report")
@RequiredArgsConstructor
public class ReportController {

    private final ReportService reportService;

    @GetMapping("/daily")
    public List<DailyAssetReportDto> getDailyReport(@RequestParam(defaultValue = "30") int days) {
        return reportService.getDailyReport(days);
    }
}

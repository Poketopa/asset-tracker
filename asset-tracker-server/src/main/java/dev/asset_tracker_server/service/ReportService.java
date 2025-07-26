package dev.asset_tracker_server.service;

import dev.asset_tracker_server.api.dto.DailyAssetReportDto;
import dev.asset_tracker_server.repository.AssetSnapshotRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ReportService {

    private final AssetSnapshotRepository assetSnapshotRepository;

    // 시스템 계정 기준 일별 리포트 조회 (향후 로그인 사용자 기준으로 확장 가능)
    private static final UUID SYSTEM_USER_ID = UUID.fromString("00000000-0000-0000-0000-000000000000");

    public List<DailyAssetReportDto> getDailyReport(int days) {
        LocalDate fromDate = LocalDate.now().minusDays(days);
        return assetSnapshotRepository.findDailyReport(SYSTEM_USER_ID, fromDate);
    }
}

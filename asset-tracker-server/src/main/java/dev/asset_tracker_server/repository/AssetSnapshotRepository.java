package dev.asset_tracker_server.repository;

import dev.asset_tracker_server.api.dto.DailyAssetReportDto;
import dev.asset_tracker_server.entity.AssetSnapshot;
import dev.asset_tracker_server.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface AssetSnapshotRepository extends JpaRepository<AssetSnapshot, UUID> {

    List<AssetSnapshot> findByUserAndSnapshotDate(User user, LocalDate snapshotDate);

    List<AssetSnapshot> findByUserOrderBySnapshotDateAsc(User user);

    List<AssetSnapshot> findByUserAndSymbolOrderBySnapshotDateAsc(User user, String symbol);

    Optional<AssetSnapshot> findByUserAndSymbolAndSnapshotDate(User user, String symbol, LocalDate snapshotDate);

    @Query("""
    SELECT new dev.asset_tracker_server.api.dto.DailyAssetReportDto(
        a.snapshotDate,
        SUM(a.totalValueKrw),
        SUM(a.totalValueUsd)
    )
    FROM AssetSnapshot a
    WHERE a.user.id = :userId AND a.snapshotDate >= :fromDate
    GROUP BY a.snapshotDate
    ORDER BY a.snapshotDate ASC
""")
    List<DailyAssetReportDto> findDailyReport(@Param("userId") UUID userId, @Param("fromDate") LocalDate fromDate);

    @Query("""
    SELECT s.snapshotDate AS date,
           SUM(s.totalValueUsd) AS totalUsd,
           SUM(s.totalValueKrw) AS totalKrw
    FROM AssetSnapshot s
    WHERE s.user = :user
    GROUP BY s.snapshotDate
    ORDER BY s.snapshotDate ASC
""")
    List<Object[]> findDailyPortfolioHistory(@Param("user") User user);
}

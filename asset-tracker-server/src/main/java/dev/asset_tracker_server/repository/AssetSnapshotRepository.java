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

@Repository
public interface AssetSnapshotRepository extends JpaRepository<AssetSnapshot, Long> {

    List<AssetSnapshot> findByUserAndSnapshotDate(User user, LocalDate snapshotDate);

    List<AssetSnapshot> findByUserOrderBySnapshotDateAsc(User user);

    List<AssetSnapshot> findByUserAndSymbolOrderBySnapshotDateAsc(User user, String symbol);

    List<AssetSnapshot> findByUserAndSymbolOrderBySnapshotDateDesc(User user, String symbol);

    List<AssetSnapshot> findByUser(User user);

    Optional<AssetSnapshot> findByUserAndSymbolAndSnapshotDate(User user, String symbol, LocalDate snapshotDate);

    @Query("SELECT new dev.asset_tracker_server.api.dto.DailyAssetReportDto(" +
            "s.snapshotDate, " +
            "SUM(s.totalValueKrw), " +
            "SUM(s.totalValueUsd)) " +
            "FROM AssetSnapshot s " +
            "WHERE s.user.id = :userId " +
            "AND s.snapshotDate >= :fromDate " +
            "GROUP BY s.snapshotDate " +
            "ORDER BY s.snapshotDate")
    List<DailyAssetReportDto> findDailyReport(@Param("userId") Long userId, @Param("fromDate") LocalDate fromDate);

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

package dev.asset_tracker_server.repository;

import dev.asset_tracker_server.entity.AssetPriceHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface AssetPriceHistoryRepository extends JpaRepository<AssetPriceHistory, Long> {

    List<AssetPriceHistory> findBySymbolAndTimestampBetween(String symbol, LocalDateTime start, LocalDateTime end);
}

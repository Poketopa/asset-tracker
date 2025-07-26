package dev.asset_tracker_server.repository;

import dev.asset_tracker_server.entity.AssetPriceHistory;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface AssetPriceHistoryRepository extends JpaRepository<AssetPriceHistory, UUID> {
    List<AssetPriceHistory> findBySymbolOrderByTimestampDesc(String symbol, Pageable pageable);
}

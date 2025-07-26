package dev.asset_tracker_server.repository;

import dev.asset_tracker_server.entity.AssetSnapshot;
import dev.asset_tracker_server.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
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
}

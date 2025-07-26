package dev.asset_tracker_server.repository;

import dev.asset_tracker_server.entity.Asset;
import dev.asset_tracker_server.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface AssetRepository extends JpaRepository<Asset, UUID> {

    List<Asset> findByUser(User user);

    Optional<Asset> findByUserAndSymbol(User user, String symbol);

    boolean existsByUserAndSymbol(User user, String symbol);
}

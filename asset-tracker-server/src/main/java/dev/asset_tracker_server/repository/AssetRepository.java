package dev.asset_tracker_server.repository;

import dev.asset_tracker_server.entity.Asset;
import dev.asset_tracker_server.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AssetRepository extends JpaRepository<Asset, Long> {

    List<Asset> findByUser(User user);
}

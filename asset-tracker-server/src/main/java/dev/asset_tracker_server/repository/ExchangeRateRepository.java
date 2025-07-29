package dev.asset_tracker_server.repository;

import dev.asset_tracker_server.entity.ExchangeRate;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ExchangeRateRepository extends JpaRepository<ExchangeRate, Long> {

    @Query("SELECT e FROM ExchangeRate e WHERE e.type = :type ORDER BY e.timestamp DESC")
    List<ExchangeRate> findLatestByType(@Param("type") String type, Pageable pageable);
}

package dev.asset_tracker_server.repository;

import dev.asset_tracker_server.entity.ExchangeRate;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface ExchangeRateRepository extends JpaRepository<ExchangeRate, UUID> {

    @Query("SELECT e FROM ExchangeRate e ORDER BY e.timestamp DESC")
    List<ExchangeRate> findAllLatest(Pageable pageable); // size = 1

    default Optional<ExchangeRate> findLatest() {
        return findAllLatest(PageRequest.of(0, 1)).stream().findFirst();
    }

    @Query("""
    SELECT e FROM ExchangeRate e
    WHERE e.type = :type
    ORDER BY e.timestamp DESC
    """)
    List<ExchangeRate> findLatestByType(@Param("type") String type, Pageable pageable);
}

package dev.asset_tracker_server.repository;

import dev.asset_tracker_server.entity.SymbolMapping;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SymbolMappingRepository extends JpaRepository<SymbolMapping, String> {

    // 키워드를 기반으로 symbol, 한글 이름, 영어 이름 검색
    @Query("""
        SELECT s FROM SymbolMapping s
        WHERE s.isActive = true AND (
              LOWER(s.symbol) LIKE LOWER(CONCAT('%', :query, '%'))
           OR LOWER(s.displayNameKr) LIKE LOWER(CONCAT('%', :query, '%'))
           OR LOWER(s.displayNameEn) LIKE LOWER(CONCAT('%', :query, '%'))
        )
        ORDER BY s.symbol ASC
        """)
    List<SymbolMapping> searchByKeyword(@Param("query") String query, Pageable pageable);
    List<SymbolMapping> findByIsActiveTrue();
}

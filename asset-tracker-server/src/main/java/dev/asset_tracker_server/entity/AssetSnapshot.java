package dev.asset_tracker_server.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

@Entity
@Table(name = "asset_snapshot",
        uniqueConstraints = @UniqueConstraint(columnNames = {"user_id", "snapshotDate", "symbol", "assetType"}))
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class AssetSnapshot extends BaseTimeEntity {

    @Id
    @Column(columnDefinition = "BINARY(16)")
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(length = 30)
    private String symbol; // null 가능

    @Enumerated(EnumType.STRING)
    @Column(length = 20)
    private AssetType assetType; // null 가능

    @Column(precision = 20, scale = 4, nullable = false)
    private BigDecimal totalValueKrw;

    @Column(precision = 20, scale = 4, nullable = false)
    private BigDecimal totalValueUsd;

    @Column(nullable = false)
    private LocalDate snapshotDate;
}

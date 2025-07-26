package dev.asset_tracker_server.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.UUID;

@Entity
@Table(name = "asset",
        uniqueConstraints = @UniqueConstraint(columnNames = {"user_id", "symbol"}))
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class Asset extends BaseTimeEntity {

    @Id
    @Column(columnDefinition = "BINARY(16)")
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(length = 30, nullable = false)
    private String symbol;

    @Column(precision = 20, scale = 8, nullable = false)
    private BigDecimal quantity;

    @Enumerated(EnumType.STRING)
    @Column(length = 20, nullable = false)
    private AssetType assetType;

    @Column(nullable = false)
    private boolean isKimchi;
}

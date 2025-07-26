package dev.asset_tracker_server.entity;

import jakarta.persistence.*;
import java.util.UUID;
import java.time.LocalDateTime;

@Entity
@Table(name = "asset_price_history")
public class AssetPriceHistory {
    @Id
    @Column(columnDefinition = "UUID")
    private UUID id;

    @Column(length = 50, nullable = false)
    private String symbol;

    @Column(nullable = false)
    private LocalDateTime timestamp;

    @Column(name = "price_usd", length = 100)
    private String priceUsd;

    @Column(name = "price_krw", length = 100)
    private String priceKrw;

    // getter, setter, 생성자 등 실무 스타일로 필요시 추가
} 
package dev.asset_tracker_server.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Setter
@Table(name = "asset_price_history")
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AssetPriceHistory {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

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

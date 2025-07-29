package dev.asset_tracker_server.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "exchange_rate")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class ExchangeRate {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(precision = 20, scale = 8, nullable = false)
    private BigDecimal usdToKrw;

    @Column(precision = 20, scale = 8, nullable = false)
    private BigDecimal usdtToKrw;

    @Column(nullable = false)
    private LocalDateTime timestamp;

    @Column(length = 20, nullable = false)
    private String type; // 예: "USD/KRW" or "USDT/KRW"

    public BigDecimal getRate() {
        return switch (type) {
            case "USD/KRW" -> usdToKrw;
            case "USDT/KRW" -> usdtToKrw;
            default -> throw new IllegalArgumentException("지원하지 않는 환율 타입: " + type);
        };
    }
}

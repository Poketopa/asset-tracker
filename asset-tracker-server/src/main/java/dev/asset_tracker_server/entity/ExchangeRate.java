package dev.asset_tracker_server.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "exchange_rate")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class ExchangeRate {

    @Id
    @Column(columnDefinition = "BINARY(16)")
    private UUID id;

    @Column(precision = 20, scale = 8, nullable = false)
    private BigDecimal usdToKrw;

    @Column(precision = 20, scale = 8, nullable = false)
    private BigDecimal usdtToKrw;

    @Column(nullable = false)
    private LocalDateTime timestamp;
}

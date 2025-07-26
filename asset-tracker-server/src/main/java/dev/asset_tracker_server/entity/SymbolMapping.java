package dev.asset_tracker_server.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "symbol_mapping")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class SymbolMapping {

    @Id
    @Column(length = 20)
    private String symbol;

    @Column(name = "display_name_kr", nullable = false)
    private String displayNameKr;

    @Column(name = "display_name_en", nullable = false)
    private String displayNameEn;

    @Enumerated(EnumType.STRING)
    @Column(length = 20, nullable = false)
    private AssetType assetType;

    @Enumerated(EnumType.STRING)
    @Column(name = "exchange", nullable = false)
    private Exchange exchange;

    @Column(nullable = false)
    private String exchangeSymbol;

    @Column(length = 30)
    private String bithumbCode;

    @Column(length = 30)
    private String upbitCode;

    @Column(length = 30)
    private String binanceCode;

    @Column(length = 30)
    private String bybitCode;

    @Column(length = 30)
    private String gateioCode;

    @Column(length = 30)
    private String finnhubCode;

    @Builder.Default
    @Column(name = "is_active", nullable = false)
    private boolean isActive = true;
}

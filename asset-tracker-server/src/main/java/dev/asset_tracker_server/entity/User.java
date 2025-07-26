package dev.asset_tracker_server.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@Entity
@Table(name = "user")
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class User extends BaseTimeEntity {

    @Id
    @Column(columnDefinition = "BINARY(16)")
    private UUID id;

    @Column(length = 50, nullable = false, unique = true)
    private String loginId;

    @Column(nullable = false)
    private String password;

    @Column(length = 100, nullable = false)
    private String nickname;

    @Column(length = 100, nullable = false, unique = true)
    private String email;
}

package dev.asset_tracker_server.api.dto;

public record UserSignupDto(
        String loginId,
        String password,
        String nickname,
        String email
) {}

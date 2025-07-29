package dev.asset_tracker_server.api.dto;

public record UserLoginDto(
        String loginId,
        String password
) {}

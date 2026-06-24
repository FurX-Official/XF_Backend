package com.xf.backend.module.auth.dto;

public record LoginResponse(
        String accessToken,
        String refreshToken,
        long expiresIn,
        String username,
        String displayName
) {}

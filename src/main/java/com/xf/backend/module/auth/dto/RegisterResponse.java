package com.xf.backend.module.auth.dto;

public record RegisterResponse(
        String id,
        String username,
        String email,
        String displayName,
        String accessToken,
        String refreshToken,
        long expiresIn
) {}

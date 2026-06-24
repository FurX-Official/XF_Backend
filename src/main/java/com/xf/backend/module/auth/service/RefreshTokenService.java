package com.xf.backend.module.auth.service;

import com.xf.backend.common.exception.BusinessException;
import com.xf.backend.common.exception.RateLimitExceededException;
import com.xf.backend.module.auth.entity.RefreshToken;
import com.xf.backend.module.auth.repository.RefreshTokenRepository;
import com.xf.backend.module.auth.security.JwtTokenProvider;
import com.xf.backend.module.user.entity.User;
import com.xf.backend.module.user.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.HexFormat;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Service
public class RefreshTokenService {

    private static final Logger log = LoggerFactory.getLogger(RefreshTokenService.class);
    private static final int MAX_LOGIN_ATTEMPTS = 5;
    private static final long LOCKOUT_DURATION_MINUTES = 15;

    private final RefreshTokenRepository refreshTokenRepository;
    private final UserRepository userRepository;
    private final JwtTokenProvider jwtTokenProvider;
    private final StringRedisTemplate redisTemplate;

    public RefreshTokenService(RefreshTokenRepository refreshTokenRepository,
                               UserRepository userRepository,
                               JwtTokenProvider jwtTokenProvider,
                               StringRedisTemplate redisTemplate) {
        this.refreshTokenRepository = refreshTokenRepository;
        this.userRepository = userRepository;
        this.jwtTokenProvider = jwtTokenProvider;
        this.redisTemplate = redisTemplate;
    }

    @Transactional
    public RefreshToken createRefreshToken(User user, String deviceInfo) {
        String family = UUID.randomUUID().toString();
        String rawToken = UUID.randomUUID().toString();
        String tokenHash = hashToken(rawToken);

        RefreshToken refreshToken = new RefreshToken();
        refreshToken.setUser(user);
        refreshToken.setTokenHash(tokenHash);
        refreshToken.setTokenFamily(family);
        refreshToken.setDeviceInfo(deviceInfo);
        refreshToken.setExpiresAt(LocalDateTime.now().plusSeconds(jwtTokenProvider.getRefreshTokenExpirationMs() / 1000));
        refreshTokenRepository.save(refreshToken);

        return refreshToken;
    }

    @Transactional
    public RefreshToken rotateRefreshToken(String rawToken, String deviceInfo) {
        String tokenHash = hashToken(rawToken);
        RefreshToken existing = refreshTokenRepository.findByTokenHash(tokenHash)
                .orElseThrow(() -> new BusinessException("INVALID_REFRESH_TOKEN", "Invalid refresh token"));

        if (existing.isRevoked()) {
            refreshTokenRepository.revokeAllByFamily(existing.getTokenFamily());
            log.warn("Refresh token reuse detected for family: {}", existing.getTokenFamily());
            throw new BusinessException("REFRESH_TOKEN_REUSE", "Refresh token reuse detected, all tokens revoked");
        }

        if (existing.isExpired()) {
            throw new BusinessException("REFRESH_TOKEN_EXPIRED", "Refresh token expired");
        }

        existing.setRevoked(true);
        refreshTokenRepository.save(existing);

        return createRefreshToken(existing.getUser(), deviceInfo);
    }

    @Transactional
    public void revokeRefreshToken(String rawToken) {
        String tokenHash = hashToken(rawToken);
        refreshTokenRepository.findByTokenHash(tokenHash).ifPresent(token -> {
            token.setRevoked(true);
            refreshTokenRepository.save(token);
        });
    }

    @Transactional
    public void revokeAllUserTokens(User user) {
        refreshTokenRepository.revokeAllByUser(user);
    }

    public void checkRateLimit(String identifier) {
        String key = "login:attempts:" + identifier;
        String attempts = redisTemplate.opsForValue().get(key);

        if (attempts != null && Integer.parseInt(attempts) >= MAX_LOGIN_ATTEMPTS) {
            throw new RateLimitExceededException("Too many login attempts. Try again in " + LOCKOUT_DURATION_MINUTES + " minutes.");
        }
    }

    public void recordFailedAttempt(String identifier) {
        String key = "login:attempts:" + identifier;
        Long attempts = redisTemplate.opsForValue().increment(key);
        if (attempts != null && attempts == 1) {
            redisTemplate.expire(key, Duration.ofMinutes(LOCKOUT_DURATION_MINUTES));
        }
    }

    public void clearFailedAttempts(String identifier) {
        redisTemplate.delete("login:attempts:" + identifier);
    }

    public Object generateTokenForOAuth2(Authentication authentication) {
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        User user = userRepository.findByUsername(userDetails.getUsername())
                .orElseThrow(() -> new BusinessException("USER_NOT_FOUND", "User not found"));

        String accessToken = jwtTokenProvider.generateAccessToken(userDetails);
        RefreshToken refreshToken = createRefreshToken(user, "oauth2");

        return new TokenResponse(accessToken, refreshToken.getTokenHash(), jwtTokenProvider.getAccessTokenExpirationMs() / 1000);
    }

    private String hashToken(String token) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(token.getBytes());
            return HexFormat.of().formatHex(hash);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }

    public record TokenResponse(String accessToken, String refreshToken, long expiresIn) {}
}

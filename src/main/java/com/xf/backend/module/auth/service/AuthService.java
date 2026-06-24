package com.xf.backend.module.auth.service;

import com.xf.backend.common.enums.AccountStatus;
import com.xf.backend.common.enums.Role;
import com.xf.backend.common.exception.BusinessException;
import com.xf.backend.common.exception.DuplicateResourceException;
import com.xf.backend.module.auth.dto.*;
import com.xf.backend.module.auth.entity.UserRole;
import com.xf.backend.module.auth.entity.RefreshToken;
import com.xf.backend.module.auth.repository.RefreshTokenRepository;
import com.xf.backend.module.auth.repository.UserRoleRepository;
import com.xf.backend.module.auth.security.JwtTokenProvider;
import com.xf.backend.module.user.entity.User;
import com.xf.backend.module.user.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;

@Service
public class AuthService {

    private static final Logger log = LoggerFactory.getLogger(AuthService.class);

    private final UserRepository userRepository;
    private final UserRoleRepository userRoleRepository;
    private final RefreshTokenService refreshTokenService;
    private final JwtTokenProvider jwtTokenProvider;
    private final AuthenticationManager authenticationManager;

    public AuthService(UserRepository userRepository,
                       UserRoleRepository userRoleRepository,
                       RefreshTokenService refreshTokenService,
                       JwtTokenProvider jwtTokenProvider,
                       AuthenticationManager authenticationManager) {
        this.userRepository = userRepository;
        this.userRoleRepository = userRoleRepository;
        this.refreshTokenService = refreshTokenService;
        this.jwtTokenProvider = jwtTokenProvider;
        this.authenticationManager = authenticationManager;
    }

    @Transactional
    public RegisterResponse register(RegisterRequest request) {
        if (userRepository.existsByUsername(request.username())) {
            throw new DuplicateResourceException("User", "username", request.username());
        }
        if (userRepository.existsByEmail(request.email())) {
            throw new DuplicateResourceException("User", "email", request.email());
        }

        User user = new User();
        user.setUsername(request.username());
        user.setEmail(request.email());
        user.setPasswordHash(new org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder(12)
                .encode(request.password()));
        user.setDisplayName(request.displayName() != null ? request.displayName() : request.username());
        user.setStatus(AccountStatus.ACTIVE);
        user = userRepository.save(user);

        UserRole userRole = new UserRole();
        userRole.setUser(user);
        userRole.setRole(Role.USER);
        userRoleRepository.save(userRole);

        user.setUserRoles(new ArrayList<>(java.util.List.of(userRole)));

        String accessToken = jwtTokenProvider.generateAccessToken(
                new org.springframework.security.core.userdetails.User(
                        user.getUsername(),
                        user.getPasswordHash(),
                        true, true, true, true,
                        java.util.Collections.singleton(
                                new org.springframework.security.core.authority.SimpleGrantedAuthority("ROLE_USER"))));

        RefreshToken refreshToken = refreshTokenService.createRefreshToken(user, "register");

        log.info("User registered: {}", user.getUsername());

        return new RegisterResponse(
                user.getId().toString(),
                user.getUsername(),
                user.getEmail(),
                user.getDisplayName(),
                accessToken,
                refreshToken.getTokenHash(),
                jwtTokenProvider.getAccessTokenExpirationMs() / 1000);
    }

    @Transactional(readOnly = true)
    public LoginResponse login(LoginRequest request, String deviceInfo) {
        refreshTokenService.checkRateLimit(request.identifier());

        Authentication authentication;
        try {
            authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(request.identifier(), request.password()));
        } catch (Exception e) {
            refreshTokenService.recordFailedAttempt(request.identifier());
            throw e;
        }

        refreshTokenService.clearFailedAttempts(request.identifier());

        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        User user = userRepository.findByUsername(userDetails.getUsername())
                .orElseThrow(() -> new BusinessException("USER_NOT_FOUND", "User not found"));

        String accessToken = jwtTokenProvider.generateAccessToken(userDetails);
        RefreshToken refreshToken = refreshTokenService.createRefreshToken(user, deviceInfo);

        return new LoginResponse(
                accessToken,
                refreshToken.getTokenHash(),
                jwtTokenProvider.getAccessTokenExpirationMs() / 1000,
                user.getUsername(),
                user.getDisplayName());
    }

    @Transactional
    public RefreshTokenService.TokenResponse refresh(RefreshRequest request, String deviceInfo) {
        RefreshToken newRefreshToken = refreshTokenService.rotateRefreshToken(request.refreshToken(), deviceInfo);

        UserDetails userDetails = new org.springframework.security.core.userdetails.User(
                newRefreshToken.getUser().getUsername(),
                newRefreshToken.getUser().getPasswordHash() != null ? newRefreshToken.getUser().getPasswordHash() : "",
                true, true, true, true,
                java.util.Collections.singleton(
                        new org.springframework.security.core.authority.SimpleGrantedAuthority(
                                "ROLE_" + newRefreshToken.getUser().getPrimaryRole().name())));

        String accessToken = jwtTokenProvider.generateAccessToken(userDetails);

        return new RefreshTokenService.TokenResponse(
                accessToken,
                newRefreshToken.getTokenHash(),
                jwtTokenProvider.getAccessTokenExpirationMs() / 1000);
    }

    @Transactional
    public void logout(String refreshToken) {
        refreshTokenService.revokeRefreshToken(refreshToken);
    }
}

package com.xf.backend.module.commission.controller;

import com.xf.backend.common.exception.ResourceNotFoundException;
import com.xf.backend.common.response.ApiResponse;
import com.xf.backend.common.response.PageResponse;
import com.xf.backend.module.commission.dto.ArtistProfileRequest;
import com.xf.backend.module.commission.dto.ArtistProfileResponse;
import com.xf.backend.module.commission.service.ArtistProfileService;
import com.xf.backend.module.user.entity.User;
import com.xf.backend.module.user.repository.UserRepository;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/artists")
public class ArtistProfileController {

    private final ArtistProfileService artistProfileService;
    private final UserRepository userRepository;

    public ArtistProfileController(ArtistProfileService artistProfileService, UserRepository userRepository) {
        this.artistProfileService = artistProfileService;
        this.userRepository = userRepository;
    }

    @PostMapping
    public ResponseEntity<ApiResponse<ArtistProfileResponse>> createProfile(
            Authentication authentication,
            @Valid @RequestBody ArtistProfileRequest request) {
        UUID userId = resolveUserId(authentication);
        ArtistProfileResponse response = artistProfileService.createProfile(userId, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.ok(response));
    }

    @PutMapping
    public ResponseEntity<ApiResponse<ArtistProfileResponse>> updateProfile(
            Authentication authentication,
            @Valid @RequestBody ArtistProfileRequest request) {
        UUID userId = resolveUserId(authentication);
        ArtistProfileResponse response = artistProfileService.updateProfile(userId, request);
        return ResponseEntity.ok(ApiResponse.ok(response));
    }

    @PatchMapping("/status")
    public ResponseEntity<ApiResponse<ArtistProfileResponse>> changeStatus(
            Authentication authentication,
            @RequestBody HashMap<String, String> body) {
        UUID userId = resolveUserId(authentication);
        com.xf.backend.common.enums.CommissionStatus status =
                com.xf.backend.common.enums.CommissionStatus.valueOf(body.get("status"));
        ArtistProfileResponse response = artistProfileService.changeStatus(userId, status);
        return ResponseEntity.ok(ApiResponse.ok(response));
    }

    @GetMapping("/{userId}")
    public ResponseEntity<ApiResponse<ArtistProfileResponse>> getProfile(@PathVariable UUID userId) {
        ArtistProfileResponse response = artistProfileService.getProfile(userId);
        return ResponseEntity.ok(ApiResponse.ok(response));
    }

    @GetMapping("/open")
    public ResponseEntity<ApiResponse<PageResponse<ArtistProfileResponse>>> listOpenArtists(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        PageResponse<ArtistProfileResponse> response = artistProfileService.listOpenArtists(page, size);
        return ResponseEntity.ok(ApiResponse.ok(response));
    }

    private UUID resolveUserId(Authentication authentication) {
        User user = userRepository.findByUsername(authentication.getName())
                .orElseThrow(() -> new ResourceNotFoundException("User", "username", authentication.getName()));
        return user.getId();
    }
}

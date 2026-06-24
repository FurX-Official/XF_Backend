package com.xf.backend.module.commission.controller;

import com.xf.backend.common.exception.ResourceNotFoundException;
import com.xf.backend.common.response.ApiResponse;
import com.xf.backend.module.commission.dto.TierRequest;
import com.xf.backend.module.commission.dto.TierResponse;
import com.xf.backend.module.commission.entity.ArtistProfile;
import com.xf.backend.module.commission.service.ArtistProfileService;
import com.xf.backend.module.commission.service.CommissionTierService;
import com.xf.backend.module.user.entity.User;
import com.xf.backend.module.user.repository.UserRepository;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/artists/tiers")
public class CommissionTierController {

    private final CommissionTierService commissionTierService;
    private final ArtistProfileService artistProfileService;
    private final UserRepository userRepository;

    public CommissionTierController(CommissionTierService commissionTierService,
                                    ArtistProfileService artistProfileService,
                                    UserRepository userRepository) {
        this.commissionTierService = commissionTierService;
        this.artistProfileService = artistProfileService;
        this.userRepository = userRepository;
    }

    @PostMapping
    public ResponseEntity<ApiResponse<TierResponse>> createTier(
            Authentication authentication,
            @Valid @RequestBody TierRequest request) {
        UUID userId = resolveUserId(authentication);
        ArtistProfile profile = artistProfileService.getProfileEntity(userId);
        TierResponse response = commissionTierService.createTier(profile, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.ok(response));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<TierResponse>> updateTier(
            @PathVariable UUID id,
            @Valid @RequestBody TierRequest request) {
        TierResponse response = commissionTierService.updateTier(id, request);
        return ResponseEntity.ok(ApiResponse.ok(response));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteTier(@PathVariable UUID id) {
        commissionTierService.deleteTier(id);
        return ResponseEntity.ok(ApiResponse.ok("Tier deleted", null));
    }

    @PatchMapping("/{id}/active")
    public ResponseEntity<ApiResponse<TierResponse>> toggleActive(@PathVariable UUID id) {
        TierResponse response = commissionTierService.toggleActive(id);
        return ResponseEntity.ok(ApiResponse.ok(response));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<TierResponse>>> listTiers(
            Authentication authentication) {
        UUID userId = resolveUserId(authentication);
        ArtistProfile profile = artistProfileService.getProfileEntity(userId);
        List<TierResponse> response = commissionTierService.listTiers(profile.getId());
        return ResponseEntity.ok(ApiResponse.ok(response));
    }

    private UUID resolveUserId(Authentication authentication) {
        User user = userRepository.findByUsername(authentication.getName())
                .orElseThrow(() -> new ResourceNotFoundException("User", "username", authentication.getName()));
        return user.getId();
    }
}

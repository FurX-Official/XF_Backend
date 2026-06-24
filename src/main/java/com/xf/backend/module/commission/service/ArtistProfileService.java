package com.xf.backend.module.commission.service;

import com.xf.backend.common.enums.CommissionStatus;
import com.xf.backend.common.exception.BusinessException;
import com.xf.backend.common.exception.ResourceNotFoundException;
import com.xf.backend.common.response.PageResponse;
import com.xf.backend.module.commission.dto.ArtistProfileRequest;
import com.xf.backend.module.commission.dto.ArtistProfileResponse;
import com.xf.backend.module.commission.entity.ArtistProfile;
import com.xf.backend.module.commission.repository.ArtistProfileRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@Transactional
public class ArtistProfileService {

    private final ArtistProfileRepository artistProfileRepository;

    public ArtistProfileService(ArtistProfileRepository artistProfileRepository) {
        this.artistProfileRepository = artistProfileRepository;
    }

    public ArtistProfileResponse createProfile(UUID userId, ArtistProfileRequest request) {
        artistProfileRepository.findByUserIdAndDeletedAtIsNull(userId)
                .ifPresent(profile -> {
                    throw new BusinessException("PROFILE_ALREADY_EXISTS", "Artist profile already exists for this user");
                });

        ArtistProfile profile = new ArtistProfile();
        profile.setUserId(userId);
        profile.setStatus(request.status());
        profile = artistProfileRepository.save(profile);
        return toResponse(profile);
    }

    public ArtistProfileResponse updateProfile(UUID userId, ArtistProfileRequest request) {
        ArtistProfile profile = artistProfileRepository.findByUserIdAndDeletedAtIsNull(userId)
                .orElseThrow(() -> new ResourceNotFoundException("ArtistProfile", "userId", userId.toString()));

        profile.setStatus(request.status());
        profile = artistProfileRepository.save(profile);
        return toResponse(profile);
    }

    public ArtistProfileResponse changeStatus(UUID userId, CommissionStatus status) {
        ArtistProfile profile = artistProfileRepository.findByUserIdAndDeletedAtIsNull(userId)
                .orElseThrow(() -> new ResourceNotFoundException("ArtistProfile", "userId", userId.toString()));

        profile.setStatus(status);
        profile = artistProfileRepository.save(profile);
        return toResponse(profile);
    }

    @Transactional(readOnly = true)
    public ArtistProfileResponse getProfile(UUID userId) {
        ArtistProfile profile = artistProfileRepository.findByUserIdAndDeletedAtIsNull(userId)
                .orElseThrow(() -> new ResourceNotFoundException("ArtistProfile", "userId", userId.toString()));
        return toResponse(profile);
    }

    @Transactional(readOnly = true)
    public PageResponse<ArtistProfileResponse> listOpenArtists(int page, int size) {
        Page<ArtistProfile> profiles = artistProfileRepository.findByStatusAndDeletedAtIsNull(
                CommissionStatus.OPEN, PageRequest.of(page, size));
        return PageResponse.of(
                profiles.getContent().stream().map(this::toResponse).toList(),
                page, size, profiles.getTotalElements());
    }

    @Transactional(readOnly = true)
    public ArtistProfile getProfileEntity(UUID userId) {
        return artistProfileRepository.findByUserIdAndDeletedAtIsNull(userId)
                .orElseThrow(() -> new ResourceNotFoundException("ArtistProfile", "userId", userId.toString()));
    }

    private ArtistProfileResponse toResponse(ArtistProfile profile) {
        return new ArtistProfileResponse(
                profile.getId(),
                profile.getUserId(),
                profile.getStatus(),
                profile.getCreatedAt(),
                profile.getUpdatedAt());
    }
}

package com.xf.backend.module.commission.service;

import com.xf.backend.common.exception.ResourceNotFoundException;
import com.xf.backend.module.commission.dto.TierRequest;
import com.xf.backend.module.commission.dto.TierResponse;
import com.xf.backend.module.commission.entity.ArtistProfile;
import com.xf.backend.module.commission.entity.CommissionTier;
import com.xf.backend.module.commission.repository.CommissionTierRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@Transactional
public class CommissionTierService {

    private final CommissionTierRepository commissionTierRepository;

    public CommissionTierService(CommissionTierRepository commissionTierRepository) {
        this.commissionTierRepository = commissionTierRepository;
    }

    public TierResponse createTier(ArtistProfile artistProfile, TierRequest request) {
        CommissionTier tier = new CommissionTier();
        tier.setArtistProfileId(artistProfile.getId());
        tier.setName(request.name());
        tier.setDescription(request.description());
        tier.setPrice(request.price());
        tier.setEstimatedDays(request.estimatedDays());
        tier = commissionTierRepository.save(tier);
        return toResponse(tier);
    }

    public TierResponse updateTier(UUID tierId, TierRequest request) {
        CommissionTier tier = commissionTierRepository.findById(tierId)
                .orElseThrow(() -> new ResourceNotFoundException("CommissionTier", "id", tierId.toString()));

        tier.setName(request.name());
        tier.setDescription(request.description());
        tier.setPrice(request.price());
        tier.setEstimatedDays(request.estimatedDays());
        tier = commissionTierRepository.save(tier);
        return toResponse(tier);
    }

    public void deleteTier(UUID tierId) {
        CommissionTier tier = commissionTierRepository.findById(tierId)
                .orElseThrow(() -> new ResourceNotFoundException("CommissionTier", "id", tierId.toString()));
        commissionTierRepository.delete(tier);
    }

    public TierResponse toggleActive(UUID tierId) {
        CommissionTier tier = commissionTierRepository.findById(tierId)
                .orElseThrow(() -> new ResourceNotFoundException("CommissionTier", "id", tierId.toString()));

        tier.setActive(!tier.isActive());
        tier = commissionTierRepository.save(tier);
        return toResponse(tier);
    }

    @Transactional(readOnly = true)
    public List<TierResponse> listTiers(UUID artistProfileId) {
        return commissionTierRepository.findByArtistProfileIdAndDeletedAtIsNull(artistProfileId)
                .stream()
                .map(this::toResponse)
                .toList();
    }

    private TierResponse toResponse(CommissionTier tier) {
        return new TierResponse(
                tier.getId(),
                tier.getArtistProfileId(),
                tier.getName(),
                tier.getDescription(),
                tier.getPrice(),
                tier.getEstimatedDays(),
                tier.isActive(),
                tier.getCreatedAt(),
                tier.getUpdatedAt());
    }
}

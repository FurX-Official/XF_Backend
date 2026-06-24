package com.xf.backend.module.commission.service;

import com.xf.backend.common.enums.CommissionRequestStatus;
import com.xf.backend.common.exception.BusinessException;
import com.xf.backend.common.exception.ResourceNotFoundException;
import com.xf.backend.common.response.PageResponse;
import com.xf.backend.module.commission.dto.CommissionRequestDTO;
import com.xf.backend.module.commission.dto.CommissionRequestResponse;
import com.xf.backend.module.commission.entity.ArtistProfile;
import com.xf.backend.module.commission.entity.CommissionRequest;
import com.xf.backend.module.commission.repository.ArtistProfileRepository;
import com.xf.backend.module.commission.repository.CommissionRequestRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@Transactional
public class CommissionRequestService {

    private final CommissionRequestRepository commissionRequestRepository;
    private final ArtistProfileRepository artistProfileRepository;

    public CommissionRequestService(CommissionRequestRepository commissionRequestRepository,
                                    ArtistProfileRepository artistProfileRepository) {
        this.commissionRequestRepository = commissionRequestRepository;
        this.artistProfileRepository = artistProfileRepository;
    }

    public CommissionRequestResponse submitRequest(UUID requesterId, CommissionRequestDTO request) {
        ArtistProfile artistProfile = artistProfileRepository.findByUserIdAndDeletedAtIsNull(request.artistId())
                .orElseThrow(() -> new ResourceNotFoundException("ArtistProfile", "userId", request.artistId().toString()));

        CommissionRequest commissionRequest = new CommissionRequest();
        commissionRequest.setRequesterId(requesterId);
        commissionRequest.setArtistProfileId(artistProfile.getId());
        commissionRequest.setTierId(request.tierId());
        commissionRequest.setDescription(request.description());
        commissionRequest.setStatus(CommissionRequestStatus.PENDING);
        commissionRequest = commissionRequestRepository.save(commissionRequest);
        return toResponse(commissionRequest);
    }

    public CommissionRequestResponse acceptRequest(UUID commissionId, UUID artistUserId) {
        CommissionRequest request = getAndValidateArtist(commissionId, artistUserId);
        validateStatusTransition(request.getStatus(), CommissionRequestStatus.ACCEPTED);
        request.setStatus(CommissionRequestStatus.ACCEPTED);
        request = commissionRequestRepository.save(request);
        return toResponse(request);
    }

    public CommissionRequestResponse rejectRequest(UUID commissionId, UUID artistUserId) {
        CommissionRequest request = getAndValidateArtist(commissionId, artistUserId);
        validateStatusTransition(request.getStatus(), CommissionRequestStatus.REJECTED);
        request.setStatus(CommissionRequestStatus.REJECTED);
        request = commissionRequestRepository.save(request);
        return toResponse(request);
    }

    public CommissionRequestResponse startWork(UUID commissionId, UUID artistUserId) {
        CommissionRequest request = getAndValidateArtist(commissionId, artistUserId);
        validateStatusTransition(request.getStatus(), CommissionRequestStatus.IN_PROGRESS);
        request.setStatus(CommissionRequestStatus.IN_PROGRESS);
        request = commissionRequestRepository.save(request);
        return toResponse(request);
    }

    public CommissionRequestResponse complete(UUID commissionId, UUID artistUserId) {
        CommissionRequest request = getAndValidateArtist(commissionId, artistUserId);
        validateStatusTransition(request.getStatus(), CommissionRequestStatus.COMPLETED);
        request.setStatus(CommissionRequestStatus.COMPLETED);
        request = commissionRequestRepository.save(request);
        return toResponse(request);
    }

    public CommissionRequestResponse cancel(UUID commissionId, UUID userId) {
        CommissionRequest request = commissionRequestRepository.findById(commissionId)
                .orElseThrow(() -> new ResourceNotFoundException("CommissionRequest", "id", commissionId.toString()));

        boolean isRequester = request.getRequesterId().equals(userId);
        boolean isArtist = isArtistOfRequest(request, userId);

        if (!isRequester && !isArtist) {
            throw new BusinessException("UNAUTHORIZED", "You are not a participant of this commission");
        }

        request.setStatus(CommissionRequestStatus.CANCELLED);
        request = commissionRequestRepository.save(request);
        return toResponse(request);
    }

    @Transactional(readOnly = true)
    public CommissionRequestResponse getRequest(UUID commissionId) {
        CommissionRequest request = commissionRequestRepository.findById(commissionId)
                .orElseThrow(() -> new ResourceNotFoundException("CommissionRequest", "id", commissionId.toString()));
        return toResponse(request);
    }

    @Transactional(readOnly = true)
    public PageResponse<CommissionRequestResponse> getMyRequests(UUID requesterId, int page, int size) {
        Page<CommissionRequest> requests = commissionRequestRepository.findByRequesterIdAndDeletedAtIsNull(
                requesterId, PageRequest.of(page, size));
        return PageResponse.of(
                requests.getContent().stream().map(this::toResponse).toList(),
                page, size, requests.getTotalElements());
    }

    @Transactional(readOnly = true)
    public PageResponse<CommissionRequestResponse> getMyCommissions(UUID artistUserId, int page, int size) {
        ArtistProfile profile = artistProfileRepository.findByUserIdAndDeletedAtIsNull(artistUserId)
                .orElseThrow(() -> new ResourceNotFoundException("ArtistProfile", "userId", artistUserId.toString()));

        Page<CommissionRequest> requests = commissionRequestRepository.findByArtistProfileIdAndDeletedAtIsNull(
                profile.getId(), PageRequest.of(page, size));
        return PageResponse.of(
                requests.getContent().stream().map(this::toResponse).toList(),
                page, size, requests.getTotalElements());
    }

    private CommissionRequest getAndValidateArtist(UUID commissionId, UUID artistUserId) {
        CommissionRequest request = commissionRequestRepository.findById(commissionId)
                .orElseThrow(() -> new ResourceNotFoundException("CommissionRequest", "id", commissionId.toString()));

        if (!isArtistOfRequest(request, artistUserId)) {
            throw new BusinessException("UNAUTHORIZED", "You are not the artist for this commission");
        }

        return request;
    }

    private boolean isArtistOfRequest(CommissionRequest request, UUID userId) {
        ArtistProfile profile = artistProfileRepository.findById(request.getArtistProfileId()).orElse(null);
        return profile != null && profile.getUserId().equals(userId);
    }

    private void validateStatusTransition(CommissionRequestStatus current, CommissionRequestStatus target) {
        boolean valid = switch (current) {
            case PENDING -> target == CommissionRequestStatus.ACCEPTED || target == CommissionRequestStatus.REJECTED;
            case ACCEPTED -> target == CommissionRequestStatus.IN_PROGRESS;
            case IN_PROGRESS -> target == CommissionRequestStatus.COMPLETED;
            default -> false;
        };
        if (!valid) {
            throw new BusinessException("INVALID_STATUS_TRANSITION",
                    "Cannot transition from " + current + " to " + target);
        }
    }

    private CommissionRequestResponse toResponse(CommissionRequest request) {
        return new CommissionRequestResponse(
                request.getId(),
                request.getRequesterId(),
                request.getArtistProfileId(),
                request.getTierId(),
                request.getDescription(),
                request.getStatus(),
                request.getCreatedAt(),
                request.getUpdatedAt());
    }
}

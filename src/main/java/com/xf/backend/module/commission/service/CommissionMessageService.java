package com.xf.backend.module.commission.service;

import com.xf.backend.common.exception.BusinessException;
import com.xf.backend.common.exception.ResourceNotFoundException;
import com.xf.backend.common.response.PageResponse;
import com.xf.backend.module.commission.dto.CommissionMessageRequest;
import com.xf.backend.module.commission.dto.CommissionMessageResponse;
import com.xf.backend.module.commission.entity.CommissionMessage;
import com.xf.backend.module.commission.entity.CommissionRequest;
import com.xf.backend.module.commission.repository.ArtistProfileRepository;
import com.xf.backend.module.commission.repository.CommissionMessageRepository;
import com.xf.backend.module.commission.repository.CommissionRequestRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@Transactional
public class CommissionMessageService {

    private final CommissionMessageRepository commissionMessageRepository;
    private final CommissionRequestRepository commissionRequestRepository;
    private final ArtistProfileRepository artistProfileRepository;

    public CommissionMessageService(CommissionMessageRepository commissionMessageRepository,
                                    CommissionRequestRepository commissionRequestRepository,
                                    ArtistProfileRepository artistProfileRepository) {
        this.commissionMessageRepository = commissionMessageRepository;
        this.commissionRequestRepository = commissionRequestRepository;
        this.artistProfileRepository = artistProfileRepository;
    }

    public CommissionMessageResponse sendMessage(UUID commissionId, UUID senderId, CommissionMessageRequest request) {
        CommissionRequest commission = commissionRequestRepository.findById(commissionId)
                .orElseThrow(() -> new ResourceNotFoundException("CommissionRequest", "id", commissionId.toString()));

        boolean isRequester = commission.getRequesterId().equals(senderId);
        boolean isArtist = artistProfileRepository.findByUserIdAndDeletedAtIsNull(senderId)
                .map(profile -> profile.getId().equals(commission.getArtistProfileId()))
                .orElse(false);

        if (!isRequester && !isArtist) {
            throw new BusinessException("UNAUTHORIZED", "You are not a participant of this commission");
        }

        CommissionMessage message = new CommissionMessage();
        message.setCommissionId(commissionId);
        message.setSenderId(senderId);
        message.setContent(request.content());
        message = commissionMessageRepository.save(message);
        return toResponse(message);
    }

    @Transactional(readOnly = true)
    public PageResponse<CommissionMessageResponse> getMessages(UUID commissionId, int page, int size) {
        commissionRequestRepository.findById(commissionId)
                .orElseThrow(() -> new ResourceNotFoundException("CommissionRequest", "id", commissionId.toString()));

        Page<CommissionMessage> messages = commissionMessageRepository.findByCommissionIdAndDeletedAtIsNull(
                commissionId, PageRequest.of(page, size));
        return PageResponse.of(
                messages.getContent().stream().map(this::toResponse).toList(),
                page, size, messages.getTotalElements());
    }

    private CommissionMessageResponse toResponse(CommissionMessage message) {
        return new CommissionMessageResponse(
                message.getId(),
                message.getCommissionId(),
                message.getSenderId(),
                message.getContent(),
                message.getCreatedAt());
    }
}

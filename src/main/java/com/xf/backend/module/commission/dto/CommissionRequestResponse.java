package com.xf.backend.module.commission.dto;

import com.xf.backend.common.enums.CommissionRequestStatus;

import java.time.LocalDateTime;
import java.util.UUID;

public record CommissionRequestResponse(
        UUID id,
        UUID requesterId,
        UUID artistProfileId,
        UUID tierId,
        String description,
        CommissionRequestStatus status,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {}

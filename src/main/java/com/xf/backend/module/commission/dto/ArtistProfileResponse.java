package com.xf.backend.module.commission.dto;

import com.xf.backend.common.enums.CommissionStatus;

import java.time.LocalDateTime;
import java.util.UUID;

public record ArtistProfileResponse(
        UUID id,
        UUID userId,
        CommissionStatus status,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {}

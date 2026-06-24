package com.xf.backend.module.commission.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

public record TierResponse(
        UUID id,
        UUID artistProfileId,
        String name,
        String description,
        BigDecimal price,
        Integer estimatedDays,
        boolean active,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {}

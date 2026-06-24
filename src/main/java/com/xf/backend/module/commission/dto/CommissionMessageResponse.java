package com.xf.backend.module.commission.dto;

import java.time.LocalDateTime;
import java.util.UUID;

public record CommissionMessageResponse(
        UUID id,
        UUID commissionId,
        UUID senderId,
        String content,
        LocalDateTime createdAt
) {}

package com.xf.backend.module.social.dto;

import java.time.LocalDateTime;
import java.util.UUID;

public record FeedEntryResponse(
        UUID id,
        UUID userId,
        String entityType,
        UUID entityId,
        LocalDateTime createdAt
) {}

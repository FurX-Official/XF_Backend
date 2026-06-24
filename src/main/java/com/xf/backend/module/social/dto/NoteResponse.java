package com.xf.backend.module.social.dto;

import java.time.LocalDateTime;
import java.util.UUID;

public record NoteResponse(
        UUID id,
        UUID senderId,
        UUID receiverId,
        String content,
        boolean read,
        LocalDateTime createdAt
) {}

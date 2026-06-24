package com.xf.backend.module.social.dto;

import java.time.LocalDateTime;
import java.util.UUID;

public record FollowResponse(
        UUID id,
        UUID followerId,
        UUID followingId,
        LocalDateTime createdAt
) {}

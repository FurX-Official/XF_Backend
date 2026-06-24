package com.xf.backend.module.submission.dto;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
public class CommentResponse {

    private UUID id;
    private UUID userId;
    private UUID submissionId;
    private String content;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}

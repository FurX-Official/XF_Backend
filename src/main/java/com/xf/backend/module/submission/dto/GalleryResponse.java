package com.xf.backend.module.submission.dto;

import com.xf.backend.common.enums.Visibility;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
public class GalleryResponse {

    private UUID id;
    private UUID ownerId;
    private String name;
    private Visibility visibility;
    private int submissionCount;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}

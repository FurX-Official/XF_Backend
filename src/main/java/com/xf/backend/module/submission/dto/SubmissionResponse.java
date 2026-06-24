package com.xf.backend.module.submission.dto;

import com.xf.backend.common.enums.ContentRating;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Getter
@Setter
public class SubmissionResponse {

    private UUID id;
    private UUID authorId;
    private String title;
    private String description;
    private ContentRating contentRating;
    private long likeCount;
    private long favoriteCount;
    private long commentCount;
    private boolean liked;
    private boolean favorited;
    private List<TagResponse> tags;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}

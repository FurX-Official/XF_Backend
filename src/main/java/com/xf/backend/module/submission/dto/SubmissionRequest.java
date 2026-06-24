package com.xf.backend.module.submission.dto;

import com.xf.backend.common.enums.ContentRating;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record SubmissionRequest(
        @NotBlank @Size(max = 255) String title,
        @Size(max = 5000) String description,
        ContentRating contentRating
) {}

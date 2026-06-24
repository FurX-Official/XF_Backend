package com.xf.backend.module.submission.dto;

import com.xf.backend.common.enums.Visibility;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record GalleryRequest(
        @NotBlank @Size(max = 128) String name,
        @NotNull Visibility visibility
) {}

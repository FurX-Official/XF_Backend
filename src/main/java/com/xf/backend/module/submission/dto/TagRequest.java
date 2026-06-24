package com.xf.backend.module.submission.dto;

import com.xf.backend.common.enums.TagType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record TagRequest(
        @NotNull TagType tagType,
        @NotBlank @Size(max = 64) String tagName
) {}

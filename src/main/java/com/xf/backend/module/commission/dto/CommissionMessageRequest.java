package com.xf.backend.module.commission.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record CommissionMessageRequest(
        @NotBlank @Size(max = 5000) String content
) {}

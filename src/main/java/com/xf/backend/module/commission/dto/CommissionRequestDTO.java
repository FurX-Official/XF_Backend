package com.xf.backend.module.commission.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.util.UUID;

public record CommissionRequestDTO(
        @NotNull UUID artistId,
        UUID tierId,
        @NotBlank @Size(max = 5000) String description
) {}

package com.xf.backend.module.commission.dto;

import com.xf.backend.common.enums.CommissionStatus;
import jakarta.validation.constraints.NotNull;

public record ArtistProfileRequest(
        @NotNull CommissionStatus status
) {}

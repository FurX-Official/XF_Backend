package com.xf.backend.module.character.dto;

import com.xf.backend.common.enums.Visibility;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record CharacterRequest(
        @NotBlank @Size(max = 64) String name,
        @Size(max = 64) String species,
        @Size(max = 32) String gender,
        @Size(max = 1000) String bio,
        Visibility visibility
) {}

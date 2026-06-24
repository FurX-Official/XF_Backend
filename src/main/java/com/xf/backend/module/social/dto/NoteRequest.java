package com.xf.backend.module.social.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.util.UUID;

public record NoteRequest(
        @NotNull UUID receiverId,
        @NotBlank @Size(max = 10000) String content
) {}

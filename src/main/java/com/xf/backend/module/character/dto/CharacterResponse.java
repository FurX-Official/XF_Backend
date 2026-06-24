package com.xf.backend.module.character.dto;

import com.xf.backend.common.enums.Visibility;
import com.xf.backend.module.character.entity.Character;

import java.time.LocalDateTime;
import java.util.UUID;

public record CharacterResponse(
        UUID id,
        UUID ownerId,
        String name,
        String species,
        String gender,
        String bio,
        Visibility visibility,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
    public static CharacterResponse from(Character character) {
        return new CharacterResponse(
                character.getId(),
                character.getOwnerId(),
                character.getName(),
                character.getSpecies(),
                character.getGender(),
                character.getBio(),
                character.getVisibility(),
                character.getCreatedAt(),
                character.getUpdatedAt()
        );
    }
}

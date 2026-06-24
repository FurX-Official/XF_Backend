package com.xf.backend.module.character.service;

import com.xf.backend.common.enums.Visibility;
import com.xf.backend.common.exception.BusinessException;
import com.xf.backend.common.exception.ResourceNotFoundException;
import com.xf.backend.common.response.PageResponse;
import com.xf.backend.module.character.dto.CharacterRequest;
import com.xf.backend.module.character.dto.CharacterResponse;
import com.xf.backend.module.character.entity.Character;
import com.xf.backend.module.character.repository.CharacterRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@Transactional
public class CharacterService {

    private final CharacterRepository characterRepository;

    public CharacterService(CharacterRepository characterRepository) {
        this.characterRepository = characterRepository;
    }

    public CharacterResponse create(UUID ownerId, CharacterRequest request) {
        Character character = new Character();
        character.setOwnerId(ownerId);
        character.setName(request.name());
        character.setSpecies(request.species());
        character.setGender(request.gender());
        character.setBio(request.bio());
        character.setVisibility(request.visibility() != null ? request.visibility() : Visibility.PUBLIC);

        characterRepository.save(character);
        return CharacterResponse.from(character);
    }

    public CharacterResponse update(UUID id, UUID ownerId, CharacterRequest request) {
        Character character = characterRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Character", "id", id.toString()));

        if (!character.getOwnerId().equals(ownerId)) {
            throw new BusinessException("NOT_AUTHORIZED", "You are not authorized to update this character");
        }

        character.setName(request.name());
        if (request.species() != null) {
            character.setSpecies(request.species());
        }
        if (request.gender() != null) {
            character.setGender(request.gender());
        }
        if (request.bio() != null) {
            character.setBio(request.bio());
        }
        if (request.visibility() != null) {
            character.setVisibility(request.visibility());
        }

        characterRepository.save(character);
        return CharacterResponse.from(character);
    }

    public void delete(UUID id, UUID ownerId) {
        Character character = characterRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Character", "id", id.toString()));

        if (!character.getOwnerId().equals(ownerId)) {
            throw new BusinessException("NOT_AUTHORIZED", "You are not authorized to delete this character");
        }

        character.softDelete();
        characterRepository.save(character);
    }

    @Transactional(readOnly = true)
    public CharacterResponse getById(UUID id) {
        Character character = characterRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Character", "id", id.toString()));

        return CharacterResponse.from(character);
    }

    @Transactional(readOnly = true)
    public PageResponse<CharacterResponse> listByOwner(UUID ownerId, Pageable pageable) {
        Page<Character> page = characterRepository.findByOwnerIdAndDeletedAtIsNull(ownerId, pageable);

        return PageResponse.of(
                page.getContent().stream().map(CharacterResponse::from).toList(),
                page.getNumber(),
                page.getSize(),
                page.getTotalElements()
        );
    }
}

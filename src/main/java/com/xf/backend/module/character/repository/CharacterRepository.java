package com.xf.backend.module.character.repository;

import com.xf.backend.module.character.entity.Character;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface CharacterRepository extends JpaRepository<Character, UUID> {

    Page<Character> findByOwnerId(UUID ownerId, Pageable pageable);

    Page<Character> findByOwnerIdAndDeletedAtIsNull(UUID ownerId, Pageable pageable);
}

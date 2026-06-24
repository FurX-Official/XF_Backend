package com.xf.backend.module.character.entity;

import com.xf.backend.common.entity.BaseEntity;
import com.xf.backend.common.enums.Visibility;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
@Entity
@Table(name = "characters", indexes = {
        @Index(name = "idx_characters_owner_id", columnList = "owner_id"),
        @Index(name = "idx_characters_species", columnList = "species")
})
public class Character extends BaseEntity {

    @Column(name = "owner_id", nullable = false)
    private UUID ownerId;

    @Column(name = "name", nullable = false, length = 64)
    private String name;

    @Column(name = "species", length = 64)
    private String species;

    @Column(name = "gender", length = 32)
    private String gender;

    @Column(name = "bio", length = 1000)
    private String bio;

    @Enumerated(EnumType.STRING)
    @Column(name = "visibility", nullable = false, length = 16)
    private Visibility visibility = Visibility.PUBLIC;
}

package com.xf.backend.module.submission.entity;

import com.xf.backend.common.entity.BaseEntity;
import com.xf.backend.common.enums.Visibility;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
@Entity
@Table(name = "galleries", indexes = {
        @Index(name = "idx_galleries_owner_id", columnList = "owner_id")
})
public class Gallery extends BaseEntity {

    @Column(name = "owner_id", nullable = false)
    private UUID ownerId;

    @Column(name = "name", nullable = false, length = 128)
    private String name;

    @Enumerated(EnumType.STRING)
    @Column(name = "visibility", nullable = false, length = 16)
    private Visibility visibility = Visibility.PUBLIC;
}

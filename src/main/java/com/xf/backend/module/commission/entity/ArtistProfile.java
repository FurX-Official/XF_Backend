package com.xf.backend.module.commission.entity;

import com.xf.backend.common.entity.BaseEntity;
import com.xf.backend.common.enums.CommissionStatus;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
@Entity
@Table(name = "artist_profiles", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"user_id"})
}, indexes = {
        @Index(name = "idx_artist_profiles_user_id", columnList = "user_id")
})
public class ArtistProfile extends BaseEntity {

    @Column(name = "user_id", nullable = false, unique = true)
    private UUID userId;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 16)
    private CommissionStatus status = CommissionStatus.CLOSED;
}

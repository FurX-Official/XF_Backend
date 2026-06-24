package com.xf.backend.module.commission.entity;

import com.xf.backend.common.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.UUID;

@Getter
@Setter
@Entity
@Table(name = "commission_tiers", indexes = {
        @Index(name = "idx_commission_tiers_artist_profile_id", columnList = "artist_profile_id")
})
public class CommissionTier extends BaseEntity {

    @Column(name = "artist_profile_id", nullable = false)
    private UUID artistProfileId;

    @Column(name = "name", nullable = false, length = 128)
    private String name;

    @Column(name = "description", length = 2000)
    private String description;

    @Column(name = "price", nullable = false, precision = 10, scale = 2)
    private BigDecimal price;

    @Column(name = "estimated_days")
    private Integer estimatedDays;

    @Column(name = "active", nullable = false)
    private boolean active = true;
}

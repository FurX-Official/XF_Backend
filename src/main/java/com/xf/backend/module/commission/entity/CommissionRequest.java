package com.xf.backend.module.commission.entity;

import com.xf.backend.common.entity.BaseEntity;
import com.xf.backend.common.enums.CommissionRequestStatus;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
@Entity
@Table(name = "commission_requests", indexes = {
        @Index(name = "idx_commission_requests_requester_id", columnList = "requester_id"),
        @Index(name = "idx_commission_requests_artist_profile_id", columnList = "artist_profile_id"),
        @Index(name = "idx_commission_requests_status", columnList = "status")
})
public class CommissionRequest extends BaseEntity {

    @Column(name = "requester_id", nullable = false)
    private UUID requesterId;

    @Column(name = "artist_profile_id", nullable = false)
    private UUID artistProfileId;

    @Column(name = "tier_id")
    private UUID tierId;

    @Column(name = "description", length = 5000)
    private String description;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 16)
    private CommissionRequestStatus status = CommissionRequestStatus.PENDING;
}

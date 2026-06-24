package com.xf.backend.module.commission.entity;

import com.xf.backend.common.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
@Entity
@Table(name = "commission_messages", indexes = {
        @Index(name = "idx_commission_messages_commission_id", columnList = "commission_id")
})
public class CommissionMessage extends BaseEntity {

    @Column(name = "commission_id", nullable = false)
    private UUID commissionId;

    @Column(name = "sender_id", nullable = false)
    private UUID senderId;

    @Column(name = "content", nullable = false, length = 5000)
    private String content;
}

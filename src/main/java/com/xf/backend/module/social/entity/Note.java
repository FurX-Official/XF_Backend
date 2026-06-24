package com.xf.backend.module.social.entity;

import com.xf.backend.common.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
@Entity
@Table(name = "notes", indexes = {
        @Index(name = "idx_notes_sender_id", columnList = "sender_id"),
        @Index(name = "idx_notes_receiver_id", columnList = "receiver_id"),
        @Index(name = "idx_notes_conversation", columnList = "sender_id, receiver_id, created_at DESC")
})
public class Note extends BaseEntity {

    @Column(name = "sender_id", nullable = false)
    private UUID senderId;

    @Column(name = "receiver_id", nullable = false)
    private UUID receiverId;

    @Column(name = "content", nullable = false, length = 10000)
    private String content;

    @Column(name = "read", nullable = false)
    private boolean read = false;
}

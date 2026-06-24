package com.xf.backend.module.social.entity;

import com.xf.backend.common.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
@Entity
@Table(name = "follows", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"follower_id", "following_id"})
}, indexes = {
        @Index(name = "idx_follows_follower_id", columnList = "follower_id"),
        @Index(name = "idx_follows_following_id", columnList = "following_id")
})
public class Follow extends BaseEntity {

    @Column(name = "follower_id", nullable = false)
    private UUID followerId;

    @Column(name = "following_id", nullable = false)
    private UUID followingId;

    @PrePersist
    @PreUpdate
    private void validate() {
        if (followerId != null && followerId.equals(followingId)) {
            throw new IllegalArgumentException("follower_id and following_id must be different");
        }
    }
}

package com.xf.backend.module.submission.entity;

import com.xf.backend.common.entity.BaseEntity;
import com.xf.backend.common.enums.ContentRating;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
@Entity
@Table(name = "submissions", indexes = {
        @Index(name = "idx_submissions_author_id", columnList = "author_id"),
        @Index(name = "idx_submissions_content_rating", columnList = "content_rating"),
        @Index(name = "idx_submissions_created_at", columnList = "created_at DESC")
})
public class Submission extends BaseEntity {

    @Column(name = "author_id", nullable = false)
    private UUID authorId;

    @Column(name = "title", nullable = false, length = 255)
    private String title;

    @Column(name = "description", length = 5000)
    private String description;

    @Enumerated(EnumType.STRING)
    @Column(name = "content_rating", nullable = false, length = 16)
    private ContentRating contentRating = ContentRating.SAFE;
}

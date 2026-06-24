package com.xf.backend.module.submission.entity;

import com.xf.backend.common.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
@Entity
@Table(name = "submission_comments", indexes = {
        @Index(name = "idx_submission_comments_submission_id", columnList = "submission_id")
})
public class SubmissionComment extends BaseEntity {

    @Column(name = "user_id", nullable = false)
    private UUID userId;

    @Column(name = "submission_id", nullable = false)
    private UUID submissionId;

    @Column(name = "content", nullable = false, length = 5000)
    private String content;
}

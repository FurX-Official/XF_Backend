package com.xf.backend.module.submission.entity;

import com.xf.backend.common.enums.TagType;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
@Entity
@Table(name = "submission_tags", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"submission_id", "tag_name"})
})
public class SubmissionTag {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "submission_id", nullable = false)
    private UUID submissionId;

    @Enumerated(EnumType.STRING)
    @Column(name = "tag_type", nullable = false, length = 16)
    private TagType tagType;

    @Column(name = "tag_name", nullable = false, length = 64)
    private String tagName;
}

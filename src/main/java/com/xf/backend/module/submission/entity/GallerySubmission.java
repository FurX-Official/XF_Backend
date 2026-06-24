package com.xf.backend.module.submission.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
@Entity
@Table(name = "gallery_submissions", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"gallery_id", "submission_id"})
})
public class GallerySubmission {

    @EmbeddedId
    private GallerySubmissionId id;

    @Column(name = "position", nullable = false)
    private Integer position;

    public GallerySubmission() {}

    public GallerySubmission(UUID galleryId, UUID submissionId, Integer position) {
        this.id = new GallerySubmissionId(galleryId, submissionId);
        this.position = position;
    }
}

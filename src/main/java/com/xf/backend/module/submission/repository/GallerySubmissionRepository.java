package com.xf.backend.module.submission.repository;

import com.xf.backend.module.submission.entity.GallerySubmission;
import com.xf.backend.module.submission.entity.GallerySubmissionId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface GallerySubmissionRepository extends JpaRepository<GallerySubmission, GallerySubmissionId> {

    List<GallerySubmission> findByGalleryIdOrderByPositionAsc(UUID galleryId);

    Optional<GallerySubmission> findByGalleryIdAndSubmissionId(UUID galleryId, UUID submissionId);

    boolean existsByGalleryIdAndSubmissionId(UUID galleryId, UUID submissionId);

    void deleteByGalleryIdAndSubmissionId(UUID galleryId, UUID submissionId);

    void deleteByGalleryId(UUID galleryId);
}

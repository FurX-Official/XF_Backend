package com.xf.backend.module.submission.repository;

import com.xf.backend.module.submission.entity.SubmissionLike;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface SubmissionLikeRepository extends JpaRepository<SubmissionLike, Long> {

    Optional<SubmissionLike> findByUserIdAndSubmissionId(UUID userId, UUID submissionId);

    boolean existsByUserIdAndSubmissionId(UUID userId, UUID submissionId);

    long countBySubmissionId(UUID submissionId);
}

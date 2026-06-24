package com.xf.backend.module.submission.repository;

import com.xf.backend.module.submission.entity.SubmissionComment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface SubmissionCommentRepository extends JpaRepository<SubmissionComment, UUID> {

    Page<SubmissionComment> findBySubmissionIdAndDeletedAtIsNull(UUID submissionId, Pageable pageable);

    long countBySubmissionIdAndDeletedAtIsNull(UUID submissionId);
}

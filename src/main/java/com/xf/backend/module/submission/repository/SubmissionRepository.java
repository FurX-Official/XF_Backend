package com.xf.backend.module.submission.repository;

import com.xf.backend.module.submission.entity.Submission;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface SubmissionRepository extends JpaRepository<Submission, UUID> {

    Page<Submission> findByAuthorIdAndDeletedAtIsNull(UUID authorId, Pageable pageable);

    Page<Submission> findAllByDeletedAtIsNull(Pageable pageable);

    long countByAuthorIdAndDeletedAtIsNull(UUID authorId);
}

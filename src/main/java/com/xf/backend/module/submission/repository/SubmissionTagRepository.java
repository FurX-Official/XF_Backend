package com.xf.backend.module.submission.repository;

import com.xf.backend.module.submission.entity.SubmissionTag;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface SubmissionTagRepository extends JpaRepository<SubmissionTag, Long> {

    List<SubmissionTag> findBySubmissionId(UUID submissionId);

    void deleteBySubmissionId(UUID submissionId);

    void deleteBySubmissionIdAndTagName(UUID submissionId, String tagName);
}

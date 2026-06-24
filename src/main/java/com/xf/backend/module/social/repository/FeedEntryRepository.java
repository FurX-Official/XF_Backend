package com.xf.backend.module.social.repository;

import com.xf.backend.module.social.entity.FeedEntry;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface FeedEntryRepository extends JpaRepository<FeedEntry, UUID> {

    Page<FeedEntry> findByUserIdOrderByCreatedAtDesc(UUID userId, Pageable pageable);
}

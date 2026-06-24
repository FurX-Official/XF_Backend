package com.xf.backend.module.submission.repository;

import com.xf.backend.module.submission.entity.Gallery;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface GalleryRepository extends JpaRepository<Gallery, UUID> {

    Page<Gallery> findByOwnerIdAndDeletedAtIsNull(UUID ownerId, Pageable pageable);

    long countByOwnerIdAndDeletedAtIsNull(UUID ownerId);
}

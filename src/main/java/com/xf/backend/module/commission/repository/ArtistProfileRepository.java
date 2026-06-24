package com.xf.backend.module.commission.repository;

import com.xf.backend.module.commission.entity.ArtistProfile;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface ArtistProfileRepository extends JpaRepository<ArtistProfile, UUID> {

    Optional<ArtistProfile> findByUserIdAndDeletedAtIsNull(UUID userId);

    Page<ArtistProfile> findByStatusAndDeletedAtIsNull(com.xf.backend.common.enums.CommissionStatus status, Pageable pageable);
}

package com.xf.backend.module.commission.repository;

import com.xf.backend.module.commission.entity.CommissionRequest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface CommissionRequestRepository extends JpaRepository<CommissionRequest, UUID> {

    Page<CommissionRequest> findByRequesterIdAndDeletedAtIsNull(UUID requesterId, Pageable pageable);

    Page<CommissionRequest> findByArtistProfileIdAndDeletedAtIsNull(UUID artistProfileId, Pageable pageable);

    Page<CommissionRequest> findByStatusAndDeletedAtIsNull(com.xf.backend.common.enums.CommissionRequestStatus status, Pageable pageable);
}

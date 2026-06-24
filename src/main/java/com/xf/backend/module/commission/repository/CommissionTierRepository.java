package com.xf.backend.module.commission.repository;

import com.xf.backend.module.commission.entity.CommissionTier;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface CommissionTierRepository extends JpaRepository<CommissionTier, UUID> {

    List<CommissionTier> findByArtistProfileIdAndDeletedAtIsNull(UUID artistProfileId);

    List<CommissionTier> findByArtistProfileIdAndActiveAndDeletedAtIsNull(UUID artistProfileId, boolean active);
}

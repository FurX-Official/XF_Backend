package com.xf.backend.module.commission.repository;

import com.xf.backend.module.commission.entity.CommissionMessage;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface CommissionMessageRepository extends JpaRepository<CommissionMessage, UUID> {

    Page<CommissionMessage> findByCommissionIdAndDeletedAtIsNull(UUID commissionId, Pageable pageable);
}

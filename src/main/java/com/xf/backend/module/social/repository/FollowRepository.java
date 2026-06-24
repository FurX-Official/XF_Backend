package com.xf.backend.module.social.repository;

import com.xf.backend.module.social.entity.Follow;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface FollowRepository extends JpaRepository<Follow, UUID> {

    Page<Follow> findByFollowerId(UUID followerId, Pageable pageable);

    Page<Follow> findByFollowingId(UUID followingId, Pageable pageable);

    boolean existsByFollowerIdAndFollowingId(UUID followerId, UUID followingId);

    long countByFollowerId(UUID followerId);

    long countByFollowingId(UUID followingId);

    @Query("SELECT f FROM Follow f WHERE f.followerId = :followerId AND f.followingId = :followingId AND f.deletedAt IS NULL")
    Optional<Follow> findActiveFollow(@Param("followerId") UUID followerId, @Param("followingId") UUID followingId);

    void deleteByFollowerIdAndFollowingId(UUID followerId, UUID followingId);
}

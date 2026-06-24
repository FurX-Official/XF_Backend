package com.xf.backend.module.social.service;

import com.xf.backend.common.exception.BusinessException;
import com.xf.backend.common.response.PageResponse;
import com.xf.backend.module.social.dto.FollowResponse;
import com.xf.backend.module.social.entity.Follow;
import com.xf.backend.module.social.repository.FollowRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@Transactional
public class FollowService {

    private final FollowRepository followRepository;

    public FollowService(FollowRepository followRepository) {
        this.followRepository = followRepository;
    }

    public FollowResponse follow(UUID followerId, UUID followingId) {
        if (followerId.equals(followingId)) {
            throw new BusinessException("CANNOT_FOLLOW_SELF", "Cannot follow yourself");
        }
        if (followRepository.existsByFollowerIdAndFollowingId(followerId, followingId)) {
            throw new BusinessException("ALREADY_FOLLOWING", "Already following this user");
        }
        Follow follow = new Follow();
        follow.setFollowerId(followerId);
        follow.setFollowingId(followingId);
        follow = followRepository.save(follow);
        return toResponse(follow);
    }

    public void unfollow(UUID followerId, UUID followingId) {
        followRepository.findActiveFollow(followerId, followingId)
                .ifPresent(Follow::softDelete);
    }

    @Transactional(readOnly = true)
    public boolean isFollowing(UUID followerId, UUID followingId) {
        return followRepository.existsByFollowerIdAndFollowingId(followerId, followingId);
    }

    @Transactional(readOnly = true)
    public PageResponse<FollowResponse> getFollowers(UUID userId, int page, int size) {
        Page<Follow> follows = followRepository.findByFollowingId(userId, PageRequest.of(page, size));
        return PageResponse.of(
                follows.getContent().stream().map(this::toResponse).toList(),
                page, size, follows.getTotalElements()
        );
    }

    @Transactional(readOnly = true)
    public PageResponse<FollowResponse> getFollowing(UUID userId, int page, int size) {
        Page<Follow> follows = followRepository.findByFollowerId(userId, PageRequest.of(page, size));
        return PageResponse.of(
                follows.getContent().stream().map(this::toResponse).toList(),
                page, size, follows.getTotalElements()
        );
    }

    @Transactional(readOnly = true)
    public long getFollowerCount(UUID userId) {
        return followRepository.countByFollowingId(userId);
    }

    @Transactional(readOnly = true)
    public long getFollowingCount(UUID userId) {
        return followRepository.countByFollowerId(userId);
    }

    private FollowResponse toResponse(Follow follow) {
        return new FollowResponse(
                follow.getId(),
                follow.getFollowerId(),
                follow.getFollowingId(),
                follow.getCreatedAt()
        );
    }
}

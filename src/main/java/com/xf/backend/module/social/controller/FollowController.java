package com.xf.backend.module.social.controller;

import com.xf.backend.common.response.ApiResponse;
import com.xf.backend.common.response.PageResponse;
import com.xf.backend.module.social.dto.FollowResponse;
import com.xf.backend.module.social.service.FollowService;
import com.xf.backend.module.user.entity.User;
import com.xf.backend.module.user.repository.UserRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/follows")
public class FollowController {

    private final FollowService followService;
    private final UserRepository userRepository;

    public FollowController(FollowService followService, UserRepository userRepository) {
        this.followService = followService;
        this.userRepository = userRepository;
    }

    @PostMapping("/{userId}")
    public ResponseEntity<ApiResponse<FollowResponse>> follow(
            @PathVariable UUID userId,
            @AuthenticationPrincipal UserDetails userDetails) {
        UUID followerId = getUserId(userDetails);
        FollowResponse response = followService.follow(followerId, userId);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.ok("Followed successfully", response));
    }

    @DeleteMapping("/{userId}")
    public ResponseEntity<ApiResponse<Void>> unfollow(
            @PathVariable UUID userId,
            @AuthenticationPrincipal UserDetails userDetails) {
        UUID followerId = getUserId(userDetails);
        followService.unfollow(followerId, userId);
        return ResponseEntity.ok(ApiResponse.ok("Unfollowed successfully", null));
    }

    @GetMapping("/{userId}/followers")
    public ResponseEntity<ApiResponse<PageResponse<FollowResponse>>> getFollowers(
            @PathVariable UUID userId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        PageResponse<FollowResponse> response = followService.getFollowers(userId, page, size);
        return ResponseEntity.ok(ApiResponse.ok(response));
    }

    @GetMapping("/{userId}/following")
    public ResponseEntity<ApiResponse<PageResponse<FollowResponse>>> getFollowing(
            @PathVariable UUID userId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        PageResponse<FollowResponse> response = followService.getFollowing(userId, page, size);
        return ResponseEntity.ok(ApiResponse.ok(response));
    }

    @GetMapping("/{userId}/count")
    public ResponseEntity<ApiResponse<Map<String, Long>>> getCount(@PathVariable UUID userId) {
        Map<String, Long> counts = new HashMap<>();
        counts.put("followers", followService.getFollowerCount(userId));
        counts.put("following", followService.getFollowingCount(userId));
        return ResponseEntity.ok(ApiResponse.ok(counts));
    }

    private UUID getUserId(UserDetails userDetails) {
        User user = userRepository.findByUsername(userDetails.getUsername())
                .orElseThrow(() -> new com.xf.backend.common.exception.ResourceNotFoundException("User", "username", userDetails.getUsername()));
        return user.getId();
    }
}

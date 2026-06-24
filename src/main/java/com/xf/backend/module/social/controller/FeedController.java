package com.xf.backend.module.social.controller;

import com.xf.backend.common.response.ApiResponse;
import com.xf.backend.common.response.PageResponse;
import com.xf.backend.module.social.dto.FeedEntryResponse;
import com.xf.backend.module.social.service.FeedService;
import com.xf.backend.module.user.entity.User;
import com.xf.backend.module.user.repository.UserRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/feed")
public class FeedController {

    private final FeedService feedService;
    private final UserRepository userRepository;

    public FeedController(FeedService feedService, UserRepository userRepository) {
        this.feedService = feedService;
        this.userRepository = userRepository;
    }

    @GetMapping
    public ResponseEntity<ApiResponse<PageResponse<FeedEntryResponse>>> getFeed(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @AuthenticationPrincipal UserDetails userDetails) {
        UUID userId = getUserId(userDetails);
        PageResponse<FeedEntryResponse> response = feedService.getFeed(userId, page, size);
        return ResponseEntity.ok(ApiResponse.ok(response));
    }

    private UUID getUserId(UserDetails userDetails) {
        User user = userRepository.findByUsername(userDetails.getUsername())
                .orElseThrow(() -> new com.xf.backend.common.exception.ResourceNotFoundException("User", "username", userDetails.getUsername()));
        return user.getId();
    }
}

package com.xf.backend.module.social.controller;

import com.xf.backend.common.response.ApiResponse;
import com.xf.backend.common.response.PageResponse;
import com.xf.backend.module.social.dto.NoteRequest;
import com.xf.backend.module.social.dto.NoteResponse;
import com.xf.backend.module.social.service.NoteService;
import com.xf.backend.module.user.entity.User;
import com.xf.backend.module.user.repository.UserRepository;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/notes")
public class NoteController {

    private final NoteService noteService;
    private final UserRepository userRepository;

    public NoteController(NoteService noteService, UserRepository userRepository) {
        this.noteService = noteService;
        this.userRepository = userRepository;
    }

    @PostMapping
    public ResponseEntity<ApiResponse<NoteResponse>> send(
            @Valid @RequestBody NoteRequest request,
            @AuthenticationPrincipal UserDetails userDetails) {
        UUID senderId = getUserId(userDetails);
        NoteResponse response = noteService.send(senderId, request.receiverId(), request.content());
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.ok("Message sent", response));
    }

    @GetMapping("/conversation/{otherUserId}")
    public ResponseEntity<ApiResponse<PageResponse<NoteResponse>>> getConversation(
            @PathVariable UUID otherUserId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @AuthenticationPrincipal UserDetails userDetails) {
        UUID userId = getUserId(userDetails);
        PageResponse<NoteResponse> response = noteService.getConversation(userId, otherUserId, page, size);
        return ResponseEntity.ok(ApiResponse.ok(response));
    }

    @GetMapping("/unread-count")
    public ResponseEntity<ApiResponse<Map<String, Long>>> getUnreadCount(
            @AuthenticationPrincipal UserDetails userDetails) {
        UUID userId = getUserId(userDetails);
        long count = noteService.getUnreadCount(userId);
        Map<String, Long> result = new HashMap<>();
        result.put("unreadCount", count);
        return ResponseEntity.ok(ApiResponse.ok(result));
    }

    @PutMapping("/{id}/read")
    public ResponseEntity<ApiResponse<Void>> markAsRead(
            @PathVariable UUID id,
            @AuthenticationPrincipal UserDetails userDetails) {
        UUID userId = getUserId(userDetails);
        noteService.markAsRead(id, userId);
        return ResponseEntity.ok(ApiResponse.ok("Marked as read", null));
    }

    private UUID getUserId(UserDetails userDetails) {
        User user = userRepository.findByUsername(userDetails.getUsername())
                .orElseThrow(() -> new com.xf.backend.common.exception.ResourceNotFoundException("User", "username", userDetails.getUsername()));
        return user.getId();
    }
}

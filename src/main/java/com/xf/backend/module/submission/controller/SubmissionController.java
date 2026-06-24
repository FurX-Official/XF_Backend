package com.xf.backend.module.submission.controller;

import com.xf.backend.common.response.ApiResponse;
import com.xf.backend.common.response.PageResponse;
import com.xf.backend.module.submission.dto.*;
import com.xf.backend.module.submission.service.SubmissionService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/submissions")
public class SubmissionController {

    private final SubmissionService submissionService;

    public SubmissionController(SubmissionService submissionService) {
        this.submissionService = submissionService;
    }

    @PostMapping
    public ResponseEntity<ApiResponse<SubmissionResponse>> createSubmission(
            @Valid @RequestBody SubmissionRequest request,
            Authentication authentication) {
        UUID userId = UUID.fromString(authentication.getName());
        SubmissionResponse response = submissionService.createSubmission(userId, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.ok(response));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<SubmissionResponse>> getSubmission(
            @PathVariable UUID id,
            Authentication authentication) {
        UUID userId = authentication != null ? UUID.fromString(authentication.getName()) : null;
        SubmissionResponse response = submissionService.getSubmission(id, userId);
        return ResponseEntity.ok(ApiResponse.ok(response));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<PageResponse<SubmissionResponse>>> listSubmissions(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            Authentication authentication) {
        UUID userId = authentication != null ? UUID.fromString(authentication.getName()) : null;
        PageResponse<SubmissionResponse> response = submissionService.listSubmissions(page, size, userId);
        return ResponseEntity.ok(ApiResponse.ok(response));
    }

    @GetMapping("/author/{authorId}")
    public ResponseEntity<ApiResponse<PageResponse<SubmissionResponse>>> listSubmissionsByAuthor(
            @PathVariable UUID authorId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            Authentication authentication) {
        UUID userId = authentication != null ? UUID.fromString(authentication.getName()) : null;
        PageResponse<SubmissionResponse> response = submissionService.listSubmissionsByAuthor(authorId, page, size, userId);
        return ResponseEntity.ok(ApiResponse.ok(response));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<SubmissionResponse>> updateSubmission(
            @PathVariable UUID id,
            @Valid @RequestBody SubmissionRequest request,
            Authentication authentication) {
        UUID userId = UUID.fromString(authentication.getName());
        SubmissionResponse response = submissionService.updateSubmission(id, userId, request);
        return ResponseEntity.ok(ApiResponse.ok(response));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteSubmission(
            @PathVariable UUID id,
            Authentication authentication) {
        UUID userId = UUID.fromString(authentication.getName());
        submissionService.deleteSubmission(id, userId);
        return ResponseEntity.ok(ApiResponse.ok("Submission deleted", null));
    }

    @PostMapping("/{id}/tags")
    public ResponseEntity<ApiResponse<TagResponse>> addTag(
            @PathVariable UUID id,
            @Valid @RequestBody TagRequest request,
            Authentication authentication) {
        UUID userId = UUID.fromString(authentication.getName());
        TagResponse response = submissionService.addTag(id, userId, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.ok(response));
    }

    @GetMapping("/{id}/tags")
    public ResponseEntity<ApiResponse<List<TagResponse>>> getTags(@PathVariable UUID id) {
        List<TagResponse> response = submissionService.getTags(id);
        return ResponseEntity.ok(ApiResponse.ok(response));
    }

    @DeleteMapping("/{id}/tags/{tagName}")
    public ResponseEntity<ApiResponse<Void>> removeTag(
            @PathVariable UUID id,
            @PathVariable String tagName,
            Authentication authentication) {
        UUID userId = UUID.fromString(authentication.getName());
        submissionService.removeTag(id, userId, tagName);
        return ResponseEntity.ok(ApiResponse.ok("Tag removed", null));
    }

    @PostMapping("/{id}/likes")
    public ResponseEntity<ApiResponse<Void>> likeSubmission(
            @PathVariable UUID id,
            Authentication authentication) {
        UUID userId = UUID.fromString(authentication.getName());
        submissionService.likeSubmission(id, userId);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.ok("Liked", null));
    }

    @DeleteMapping("/{id}/likes")
    public ResponseEntity<ApiResponse<Void>> unlikeSubmission(
            @PathVariable UUID id,
            Authentication authentication) {
        UUID userId = UUID.fromString(authentication.getName());
        submissionService.unlikeSubmission(id, userId);
        return ResponseEntity.ok(ApiResponse.ok("Unliked", null));
    }

    @PostMapping("/{id}/favorites")
    public ResponseEntity<ApiResponse<Void>> favoriteSubmission(
            @PathVariable UUID id,
            Authentication authentication) {
        UUID userId = UUID.fromString(authentication.getName());
        submissionService.favoriteSubmission(id, userId);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.ok("Favorited", null));
    }

    @DeleteMapping("/{id}/favorites")
    public ResponseEntity<ApiResponse<Void>> unfavoriteSubmission(
            @PathVariable UUID id,
            Authentication authentication) {
        UUID userId = UUID.fromString(authentication.getName());
        submissionService.unfavoriteSubmission(id, userId);
        return ResponseEntity.ok(ApiResponse.ok("Unfavorited", null));
    }

    @PostMapping("/{id}/comments")
    public ResponseEntity<ApiResponse<CommentResponse>> addComment(
            @PathVariable UUID id,
            @Valid @RequestBody CommentRequest request,
            Authentication authentication) {
        UUID userId = UUID.fromString(authentication.getName());
        CommentResponse response = submissionService.addComment(id, userId, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.ok(response));
    }

    @GetMapping("/{id}/comments")
    public ResponseEntity<ApiResponse<PageResponse<CommentResponse>>> getComments(
            @PathVariable UUID id,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        PageResponse<CommentResponse> response = submissionService.getComments(id, page, size);
        return ResponseEntity.ok(ApiResponse.ok(response));
    }

    @PutMapping("/comments/{commentId}")
    public ResponseEntity<ApiResponse<CommentResponse>> updateComment(
            @PathVariable UUID commentId,
            @Valid @RequestBody CommentRequest request,
            Authentication authentication) {
        UUID userId = UUID.fromString(authentication.getName());
        CommentResponse response = submissionService.updateComment(commentId, userId, request);
        return ResponseEntity.ok(ApiResponse.ok(response));
    }

    @DeleteMapping("/comments/{commentId}")
    public ResponseEntity<ApiResponse<Void>> deleteComment(
            @PathVariable UUID commentId,
            Authentication authentication) {
        UUID userId = UUID.fromString(authentication.getName());
        submissionService.deleteComment(commentId, userId);
        return ResponseEntity.ok(ApiResponse.ok("Comment deleted", null));
    }
}

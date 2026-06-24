package com.xf.backend.module.submission.controller;

import com.xf.backend.common.response.ApiResponse;
import com.xf.backend.common.response.PageResponse;
import com.xf.backend.module.submission.dto.GalleryRequest;
import com.xf.backend.module.submission.dto.GalleryResponse;
import com.xf.backend.module.submission.service.GalleryService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/galleries")
public class GalleryController {

    private final GalleryService galleryService;

    public GalleryController(GalleryService galleryService) {
        this.galleryService = galleryService;
    }

    @PostMapping
    public ResponseEntity<ApiResponse<GalleryResponse>> createGallery(
            @Valid @RequestBody GalleryRequest request,
            Authentication authentication) {
        UUID userId = UUID.fromString(authentication.getName());
        GalleryResponse response = galleryService.createGallery(userId, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.ok(response));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<GalleryResponse>> getGallery(@PathVariable UUID id) {
        GalleryResponse response = galleryService.getGallery(id);
        return ResponseEntity.ok(ApiResponse.ok(response));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<PageResponse<GalleryResponse>>> listGalleries(
            @RequestParam UUID ownerId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        PageResponse<GalleryResponse> response = galleryService.listGalleries(ownerId, page, size);
        return ResponseEntity.ok(ApiResponse.ok(response));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<GalleryResponse>> updateGallery(
            @PathVariable UUID id,
            @Valid @RequestBody GalleryRequest request,
            Authentication authentication) {
        UUID userId = UUID.fromString(authentication.getName());
        GalleryResponse response = galleryService.updateGallery(id, userId, request);
        return ResponseEntity.ok(ApiResponse.ok(response));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteGallery(
            @PathVariable UUID id,
            Authentication authentication) {
        UUID userId = UUID.fromString(authentication.getName());
        galleryService.deleteGallery(id, userId);
        return ResponseEntity.ok(ApiResponse.ok("Gallery deleted", null));
    }

    @PostMapping("/{id}/submissions/{submissionId}")
    public ResponseEntity<ApiResponse<Void>> addSubmissionToGallery(
            @PathVariable UUID id,
            @PathVariable UUID submissionId,
            Authentication authentication) {
        UUID userId = UUID.fromString(authentication.getName());
        galleryService.addSubmissionToGallery(id, submissionId, userId);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.ok("Submission added to gallery", null));
    }

    @DeleteMapping("/{id}/submissions/{submissionId}")
    public ResponseEntity<ApiResponse<Void>> removeSubmissionFromGallery(
            @PathVariable UUID id,
            @PathVariable UUID submissionId,
            Authentication authentication) {
        UUID userId = UUID.fromString(authentication.getName());
        galleryService.removeSubmissionFromGallery(id, submissionId, userId);
        return ResponseEntity.ok(ApiResponse.ok("Submission removed from gallery", null));
    }

    @GetMapping("/{id}/submissions")
    public ResponseEntity<ApiResponse<List<UUID>>> getGallerySubmissions(@PathVariable UUID id) {
        List<UUID> response = galleryService.getGallerySubmissions(id);
        return ResponseEntity.ok(ApiResponse.ok(response));
    }
}

package com.xf.backend.module.commission.controller;

import com.xf.backend.common.exception.ResourceNotFoundException;
import com.xf.backend.common.response.ApiResponse;
import com.xf.backend.common.response.PageResponse;
import com.xf.backend.module.commission.dto.CommissionRequestDTO;
import com.xf.backend.module.commission.dto.CommissionRequestResponse;
import com.xf.backend.module.commission.service.CommissionRequestService;
import com.xf.backend.module.user.entity.User;
import com.xf.backend.module.user.repository.UserRepository;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/commissions")
public class CommissionRequestController {

    private final CommissionRequestService commissionRequestService;
    private final UserRepository userRepository;

    public CommissionRequestController(CommissionRequestService commissionRequestService,
                                       UserRepository userRepository) {
        this.commissionRequestService = commissionRequestService;
        this.userRepository = userRepository;
    }

    @PostMapping
    public ResponseEntity<ApiResponse<CommissionRequestResponse>> submitRequest(
            Authentication authentication,
            @Valid @RequestBody CommissionRequestDTO request) {
        UUID userId = resolveUserId(authentication);
        CommissionRequestResponse response = commissionRequestService.submitRequest(userId, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.ok(response));
    }

    @PatchMapping("/{id}/accept")
    public ResponseEntity<ApiResponse<CommissionRequestResponse>> acceptRequest(
            Authentication authentication,
            @PathVariable UUID id) {
        UUID userId = resolveUserId(authentication);
        CommissionRequestResponse response = commissionRequestService.acceptRequest(id, userId);
        return ResponseEntity.ok(ApiResponse.ok(response));
    }

    @PatchMapping("/{id}/reject")
    public ResponseEntity<ApiResponse<CommissionRequestResponse>> rejectRequest(
            Authentication authentication,
            @PathVariable UUID id) {
        UUID userId = resolveUserId(authentication);
        CommissionRequestResponse response = commissionRequestService.rejectRequest(id, userId);
        return ResponseEntity.ok(ApiResponse.ok(response));
    }

    @PatchMapping("/{id}/start")
    public ResponseEntity<ApiResponse<CommissionRequestResponse>> startWork(
            Authentication authentication,
            @PathVariable UUID id) {
        UUID userId = resolveUserId(authentication);
        CommissionRequestResponse response = commissionRequestService.startWork(id, userId);
        return ResponseEntity.ok(ApiResponse.ok(response));
    }

    @PatchMapping("/{id}/complete")
    public ResponseEntity<ApiResponse<CommissionRequestResponse>> complete(
            Authentication authentication,
            @PathVariable UUID id) {
        UUID userId = resolveUserId(authentication);
        CommissionRequestResponse response = commissionRequestService.complete(id, userId);
        return ResponseEntity.ok(ApiResponse.ok(response));
    }

    @PatchMapping("/{id}/cancel")
    public ResponseEntity<ApiResponse<CommissionRequestResponse>> cancel(
            Authentication authentication,
            @PathVariable UUID id) {
        UUID userId = resolveUserId(authentication);
        CommissionRequestResponse response = commissionRequestService.cancel(id, userId);
        return ResponseEntity.ok(ApiResponse.ok(response));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<CommissionRequestResponse>> getRequest(@PathVariable UUID id) {
        CommissionRequestResponse response = commissionRequestService.getRequest(id);
        return ResponseEntity.ok(ApiResponse.ok(response));
    }

    @GetMapping("/my-requests")
    public ResponseEntity<ApiResponse<PageResponse<CommissionRequestResponse>>> getMyRequests(
            Authentication authentication,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        UUID userId = resolveUserId(authentication);
        PageResponse<CommissionRequestResponse> response = commissionRequestService.getMyRequests(userId, page, size);
        return ResponseEntity.ok(ApiResponse.ok(response));
    }

    @GetMapping("/my-commissions")
    public ResponseEntity<ApiResponse<PageResponse<CommissionRequestResponse>>> getMyCommissions(
            Authentication authentication,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        UUID userId = resolveUserId(authentication);
        PageResponse<CommissionRequestResponse> response = commissionRequestService.getMyCommissions(userId, page, size);
        return ResponseEntity.ok(ApiResponse.ok(response));
    }

    private UUID resolveUserId(Authentication authentication) {
        User user = userRepository.findByUsername(authentication.getName())
                .orElseThrow(() -> new ResourceNotFoundException("User", "username", authentication.getName()));
        return user.getId();
    }
}

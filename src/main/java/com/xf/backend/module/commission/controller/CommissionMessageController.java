package com.xf.backend.module.commission.controller;

import com.xf.backend.common.exception.ResourceNotFoundException;
import com.xf.backend.common.response.ApiResponse;
import com.xf.backend.common.response.PageResponse;
import com.xf.backend.module.commission.dto.CommissionMessageRequest;
import com.xf.backend.module.commission.dto.CommissionMessageResponse;
import com.xf.backend.module.commission.service.CommissionMessageService;
import com.xf.backend.module.user.entity.User;
import com.xf.backend.module.user.repository.UserRepository;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/commissions/{commissionId}/messages")
public class CommissionMessageController {

    private final CommissionMessageService commissionMessageService;
    private final UserRepository userRepository;

    public CommissionMessageController(CommissionMessageService commissionMessageService,
                                       UserRepository userRepository) {
        this.commissionMessageService = commissionMessageService;
        this.userRepository = userRepository;
    }

    @PostMapping
    public ResponseEntity<ApiResponse<CommissionMessageResponse>> sendMessage(
            Authentication authentication,
            @PathVariable UUID commissionId,
            @Valid @RequestBody CommissionMessageRequest request) {
        UUID userId = resolveUserId(authentication);
        CommissionMessageResponse response = commissionMessageService.sendMessage(commissionId, userId, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.ok(response));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<PageResponse<CommissionMessageResponse>>> getMessages(
            @PathVariable UUID commissionId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "50") int size) {
        PageResponse<CommissionMessageResponse> response = commissionMessageService.getMessages(commissionId, page, size);
        return ResponseEntity.ok(ApiResponse.ok(response));
    }

    private UUID resolveUserId(Authentication authentication) {
        User user = userRepository.findByUsername(authentication.getName())
                .orElseThrow(() -> new ResourceNotFoundException("User", "username", authentication.getName()));
        return user.getId();
    }
}

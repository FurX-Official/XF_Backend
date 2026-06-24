package com.xf.backend.module.character.controller;

import com.xf.backend.common.exception.ResourceNotFoundException;
import com.xf.backend.common.response.ApiResponse;
import com.xf.backend.common.response.PageResponse;
import com.xf.backend.module.character.dto.CharacterRequest;
import com.xf.backend.module.character.dto.CharacterResponse;
import com.xf.backend.module.character.service.CharacterService;
import com.xf.backend.module.user.entity.User;
import com.xf.backend.module.user.repository.UserRepository;
import jakarta.validation.Valid;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/characters")
public class CharacterController {

    private final CharacterService characterService;
    private final UserRepository userRepository;

    public CharacterController(CharacterService characterService, UserRepository userRepository) {
        this.characterService = characterService;
        this.userRepository = userRepository;
    }

    @PostMapping
    public ResponseEntity<ApiResponse<CharacterResponse>> create(
            @Valid @RequestBody CharacterRequest request,
            Authentication authentication) {
        UUID ownerId = resolveOwnerId(authentication);
        CharacterResponse response = characterService.create(ownerId, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.ok("Character created", response));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<CharacterResponse>> update(
            @PathVariable UUID id,
            @Valid @RequestBody CharacterRequest request,
            Authentication authentication) {
        UUID ownerId = resolveOwnerId(authentication);
        CharacterResponse response = characterService.update(id, ownerId, request);
        return ResponseEntity.ok(ApiResponse.ok("Character updated", response));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> delete(
            @PathVariable UUID id,
            Authentication authentication) {
        UUID ownerId = resolveOwnerId(authentication);
        characterService.delete(id, ownerId);
        return ResponseEntity.ok(ApiResponse.ok("Character deleted", null));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<CharacterResponse>> getById(@PathVariable UUID id) {
        CharacterResponse response = characterService.getById(id);
        return ResponseEntity.ok(ApiResponse.ok(response));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<PageResponse<CharacterResponse>>> listByOwner(
            @RequestParam UUID ownerId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));
        PageResponse<CharacterResponse> response = characterService.listByOwner(ownerId, pageable);
        return ResponseEntity.ok(ApiResponse.ok(response));
    }

    private UUID resolveOwnerId(Authentication authentication) {
        String username = authentication.getName();
        User user = userRepository.findByUsernameOrEmail(username, username)
                .orElseThrow(() -> new ResourceNotFoundException("User", "username", username));
        return user.getId();
    }
}

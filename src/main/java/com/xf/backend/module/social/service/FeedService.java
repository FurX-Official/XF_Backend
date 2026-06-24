package com.xf.backend.module.social.service;

import com.xf.backend.common.response.PageResponse;
import com.xf.backend.module.social.dto.FeedEntryResponse;
import com.xf.backend.module.social.entity.FeedEntry;
import com.xf.backend.module.social.repository.FeedEntryRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@Transactional
public class FeedService {

    private final FeedEntryRepository feedEntryRepository;

    public FeedService(FeedEntryRepository feedEntryRepository) {
        this.feedEntryRepository = feedEntryRepository;
    }

    public FeedEntryResponse addEntry(UUID userId, String entityType, UUID entityId) {
        FeedEntry entry = new FeedEntry();
        entry.setUserId(userId);
        entry.setEntityType(entityType);
        entry.setEntityId(entityId);
        entry = feedEntryRepository.save(entry);
        return toResponse(entry);
    }

    @Transactional(readOnly = true)
    public PageResponse<FeedEntryResponse> getFeed(UUID userId, int page, int size) {
        Page<FeedEntry> entries = feedEntryRepository.findByUserIdOrderByCreatedAtDesc(userId, PageRequest.of(page, size));
        return PageResponse.of(
                entries.getContent().stream().map(this::toResponse).toList(),
                page, size, entries.getTotalElements()
        );
    }

    private FeedEntryResponse toResponse(FeedEntry entry) {
        return new FeedEntryResponse(
                entry.getId(),
                entry.getUserId(),
                entry.getEntityType(),
                entry.getEntityId(),
                entry.getCreatedAt()
        );
    }
}

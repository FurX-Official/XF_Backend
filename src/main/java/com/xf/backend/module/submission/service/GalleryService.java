package com.xf.backend.module.submission.service;

import com.xf.backend.common.exception.BusinessException;
import com.xf.backend.common.exception.DuplicateResourceException;
import com.xf.backend.common.exception.ResourceNotFoundException;
import com.xf.backend.common.response.PageResponse;
import com.xf.backend.module.submission.dto.GalleryRequest;
import com.xf.backend.module.submission.dto.GalleryResponse;
import com.xf.backend.module.submission.entity.Gallery;
import com.xf.backend.module.submission.entity.GallerySubmission;
import com.xf.backend.module.submission.entity.GallerySubmissionId;
import com.xf.backend.module.submission.entity.Submission;
import com.xf.backend.module.submission.repository.GalleryRepository;
import com.xf.backend.module.submission.repository.GallerySubmissionRepository;
import com.xf.backend.module.submission.repository.SubmissionRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@Transactional
public class GalleryService {

    private final GalleryRepository galleryRepository;
    private final GallerySubmissionRepository gallerySubmissionRepository;
    private final SubmissionRepository submissionRepository;

    public GalleryService(GalleryRepository galleryRepository,
                          GallerySubmissionRepository gallerySubmissionRepository,
                          SubmissionRepository submissionRepository) {
        this.galleryRepository = galleryRepository;
        this.gallerySubmissionRepository = gallerySubmissionRepository;
        this.submissionRepository = submissionRepository;
    }

    public GalleryResponse createGallery(UUID ownerId, GalleryRequest request) {
        Gallery gallery = new Gallery();
        gallery.setOwnerId(ownerId);
        gallery.setName(request.name());
        gallery.setVisibility(request.visibility());
        gallery = galleryRepository.save(gallery);
        return toResponse(gallery);
    }

    @Transactional(readOnly = true)
    public GalleryResponse getGallery(UUID id) {
        Gallery gallery = findGallery(id);
        return toResponse(gallery);
    }

    public PageResponse<GalleryResponse> listGalleries(UUID ownerId, int page, int size) {
        PageRequest pageRequest = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));
        Page<Gallery> galleries = galleryRepository.findByOwnerIdAndDeletedAtIsNull(ownerId, pageRequest);
        List<GalleryResponse> content = galleries.getContent().stream()
                .map(this::toResponse)
                .toList();
        return PageResponse.of(content, page, size, galleries.getTotalElements());
    }

    public GalleryResponse updateGallery(UUID id, UUID ownerId, GalleryRequest request) {
        Gallery gallery = findGallery(id);
        if (!gallery.getOwnerId().equals(ownerId)) {
            throw new BusinessException("UNAUTHORIZED", "You can only update your own galleries");
        }
        gallery.setName(request.name());
        gallery.setVisibility(request.visibility());
        gallery = galleryRepository.save(gallery);
        return toResponse(gallery);
    }

    public void deleteGallery(UUID id, UUID ownerId) {
        Gallery gallery = findGallery(id);
        if (!gallery.getOwnerId().equals(ownerId)) {
            throw new BusinessException("UNAUTHORIZED", "You can only delete your own galleries");
        }
        gallerySubmissionRepository.deleteByGalleryId(id);
        gallery.softDelete();
        galleryRepository.save(gallery);
    }

    public void addSubmissionToGallery(UUID galleryId, UUID submissionId, UUID ownerId) {
        Gallery gallery = findGallery(galleryId);
        if (!gallery.getOwnerId().equals(ownerId)) {
            throw new BusinessException("UNAUTHORIZED", "You can only modify your own galleries");
        }
        submissionRepository.findById(submissionId)
                .filter(s -> !s.isDeleted())
                .orElseThrow(() -> new ResourceNotFoundException("Submission", "id", submissionId.toString()));
        if (gallerySubmissionRepository.existsByGalleryIdAndSubmissionId(galleryId, submissionId)) {
            throw new DuplicateResourceException("GallerySubmission", "submission", submissionId.toString());
        }
        List<GallerySubmission> existing = gallerySubmissionRepository.findByGalleryIdOrderByPositionAsc(galleryId);
        int nextPosition = existing.isEmpty() ? 0 : existing.getLast().getPosition() + 1;
        GallerySubmission gs = new GallerySubmission(galleryId, submissionId, nextPosition);
        gallerySubmissionRepository.save(gs);
    }

    public void removeSubmissionFromGallery(UUID galleryId, UUID submissionId, UUID ownerId) {
        Gallery gallery = findGallery(galleryId);
        if (!gallery.getOwnerId().equals(ownerId)) {
            throw new BusinessException("UNAUTHORIZED", "You can only modify your own galleries");
        }
        gallerySubmissionRepository.deleteByGalleryIdAndSubmissionId(galleryId, submissionId);
    }

    @Transactional(readOnly = true)
    public List<UUID> getGallerySubmissions(UUID galleryId) {
        findGallery(galleryId);
        return gallerySubmissionRepository.findByGalleryIdOrderByPositionAsc(galleryId).stream()
                .map(gs -> gs.getId().getSubmissionId())
                .toList();
    }

    private Gallery findGallery(UUID id) {
        return galleryRepository.findById(id)
                .filter(g -> !g.isDeleted())
                .orElseThrow(() -> new ResourceNotFoundException("Gallery", "id", id.toString()));
    }

    private GalleryResponse toResponse(Gallery gallery) {
        GalleryResponse response = new GalleryResponse();
        response.setId(gallery.getId());
        response.setOwnerId(gallery.getOwnerId());
        response.setName(gallery.getName());
        response.setVisibility(gallery.getVisibility());
        response.setCreatedAt(gallery.getCreatedAt());
        response.setUpdatedAt(gallery.getUpdatedAt());
        List<GallerySubmission> gs = gallerySubmissionRepository.findByGalleryIdOrderByPositionAsc(gallery.getId());
        response.setSubmissionCount(gs.size());
        return response;
    }
}

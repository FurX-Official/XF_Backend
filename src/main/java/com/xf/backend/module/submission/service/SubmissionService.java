package com.xf.backend.module.submission.service;

import com.xf.backend.common.enums.ContentRating;
import com.xf.backend.common.exception.BusinessException;
import com.xf.backend.common.exception.DuplicateResourceException;
import com.xf.backend.common.exception.ResourceNotFoundException;
import com.xf.backend.common.response.PageResponse;
import com.xf.backend.module.submission.dto.*;
import com.xf.backend.module.submission.entity.Submission;
import com.xf.backend.module.submission.entity.SubmissionComment;
import com.xf.backend.module.submission.entity.SubmissionFavorite;
import com.xf.backend.module.submission.entity.SubmissionLike;
import com.xf.backend.module.submission.entity.SubmissionTag;
import com.xf.backend.module.submission.repository.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@Transactional
public class SubmissionService {

    private final SubmissionRepository submissionRepository;
    private final SubmissionTagRepository tagRepository;
    private final SubmissionLikeRepository likeRepository;
    private final SubmissionFavoriteRepository favoriteRepository;
    private final SubmissionCommentRepository commentRepository;

    public SubmissionService(SubmissionRepository submissionRepository,
                             SubmissionTagRepository tagRepository,
                             SubmissionLikeRepository likeRepository,
                             SubmissionFavoriteRepository favoriteRepository,
                             SubmissionCommentRepository commentRepository) {
        this.submissionRepository = submissionRepository;
        this.tagRepository = tagRepository;
        this.likeRepository = likeRepository;
        this.favoriteRepository = favoriteRepository;
        this.commentRepository = commentRepository;
    }

    public SubmissionResponse createSubmission(UUID authorId, SubmissionRequest request) {
        Submission submission = new Submission();
        submission.setAuthorId(authorId);
        submission.setTitle(request.title());
        submission.setDescription(request.description());
        submission.setContentRating(
                request.contentRating() != null ? request.contentRating() : ContentRating.SAFE);
        submission = submissionRepository.save(submission);
        return toResponse(submission, authorId);
    }

    @Transactional(readOnly = true)
    public SubmissionResponse getSubmission(UUID id, UUID currentUserId) {
        Submission submission = findSubmission(id);
        return toResponse(submission, currentUserId);
    }

    public PageResponse<SubmissionResponse> listSubmissions(int page, int size, UUID currentUserId) {
        PageRequest pageRequest = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));
        Page<Submission> submissions = submissionRepository.findAllByDeletedAtIsNull(pageRequest);
        List<SubmissionResponse> content = submissions.getContent().stream()
                .map(s -> toResponse(s, currentUserId))
                .toList();
        return PageResponse.of(content, page, size, submissions.getTotalElements());
    }

    public PageResponse<SubmissionResponse> listSubmissionsByAuthor(UUID authorId, int page, int size, UUID currentUserId) {
        PageRequest pageRequest = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));
        Page<Submission> submissions = submissionRepository.findByAuthorIdAndDeletedAtIsNull(authorId, pageRequest);
        List<SubmissionResponse> content = submissions.getContent().stream()
                .map(s -> toResponse(s, currentUserId))
                .toList();
        return PageResponse.of(content, page, size, submissions.getTotalElements());
    }

    public SubmissionResponse updateSubmission(UUID id, UUID authorId, SubmissionRequest request) {
        Submission submission = findSubmission(id);
        if (!submission.getAuthorId().equals(authorId)) {
            throw new BusinessException("UNAUTHORIZED", "You can only update your own submissions");
        }
        submission.setTitle(request.title());
        submission.setDescription(request.description());
        if (request.contentRating() != null) {
            submission.setContentRating(request.contentRating());
        }
        submission = submissionRepository.save(submission);
        return toResponse(submission, authorId);
    }

    public void deleteSubmission(UUID id, UUID authorId) {
        Submission submission = findSubmission(id);
        if (!submission.getAuthorId().equals(authorId)) {
            throw new BusinessException("UNAUTHORIZED", "You can only delete your own submissions");
        }
        submission.softDelete();
        submissionRepository.save(submission);
    }

    public TagResponse addTag(UUID submissionId, UUID userId, TagRequest request) {
        Submission submission = findSubmission(submissionId);
        if (!submission.getAuthorId().equals(userId)) {
            throw new BusinessException("UNAUTHORIZED", "You can only tag your own submissions");
        }
        List<SubmissionTag> existingTags = tagRepository.findBySubmissionId(submissionId);
        boolean exists = existingTags.stream()
                .anyMatch(t -> t.getTagName().equalsIgnoreCase(request.tagName()));
        if (exists) {
            throw new DuplicateResourceException("Tag", "tagName", request.tagName());
        }
        SubmissionTag tag = new SubmissionTag();
        tag.setSubmissionId(submissionId);
        tag.setTagType(request.tagType());
        tag.setTagName(request.tagName().toLowerCase());
        tag = tagRepository.save(tag);
        return toTagResponse(tag);
    }

    public List<TagResponse> getTags(UUID submissionId) {
        return tagRepository.findBySubmissionId(submissionId).stream()
                .map(this::toTagResponse)
                .toList();
    }

    public void removeTag(UUID submissionId, UUID userId, String tagName) {
        Submission submission = findSubmission(submissionId);
        if (!submission.getAuthorId().equals(userId)) {
            throw new BusinessException("UNAUTHORIZED", "You can only remove tags from your own submissions");
        }
        tagRepository.deleteBySubmissionIdAndTagName(submissionId, tagName.toLowerCase());
    }

    public void likeSubmission(UUID submissionId, UUID userId) {
        findSubmission(submissionId);
        if (likeRepository.existsByUserIdAndSubmissionId(userId, submissionId)) {
            throw new DuplicateResourceException("Like", "submission", submissionId.toString());
        }
        SubmissionLike like = new SubmissionLike();
        like.setUserId(userId);
        like.setSubmissionId(submissionId);
        likeRepository.save(like);
    }

    public void unlikeSubmission(UUID submissionId, UUID userId) {
        SubmissionLike like = likeRepository.findByUserIdAndSubmissionId(userId, submissionId)
                .orElseThrow(() -> new ResourceNotFoundException("Like", "submission", submissionId.toString()));
        likeRepository.delete(like);
    }

    public void favoriteSubmission(UUID submissionId, UUID userId) {
        findSubmission(submissionId);
        if (favoriteRepository.existsByUserIdAndSubmissionId(userId, submissionId)) {
            throw new DuplicateResourceException("Favorite", "submission", submissionId.toString());
        }
        SubmissionFavorite favorite = new SubmissionFavorite();
        favorite.setUserId(userId);
        favorite.setSubmissionId(submissionId);
        favoriteRepository.save(favorite);
    }

    public void unfavoriteSubmission(UUID submissionId, UUID userId) {
        SubmissionFavorite favorite = favoriteRepository.findByUserIdAndSubmissionId(userId, submissionId)
                .orElseThrow(() -> new ResourceNotFoundException("Favorite", "submission", submissionId.toString()));
        favoriteRepository.delete(favorite);
    }

    public CommentResponse addComment(UUID submissionId, UUID userId, CommentRequest request) {
        findSubmission(submissionId);
        SubmissionComment comment = new SubmissionComment();
        comment.setUserId(userId);
        comment.setSubmissionId(submissionId);
        comment.setContent(request.content());
        comment = commentRepository.save(comment);
        return toCommentResponse(comment);
    }

    public PageResponse<CommentResponse> getComments(UUID submissionId, int page, int size) {
        PageRequest pageRequest = PageRequest.of(page, size, Sort.by(Sort.Direction.ASC, "createdAt"));
        Page<SubmissionComment> comments = commentRepository.findBySubmissionIdAndDeletedAtIsNull(submissionId, pageRequest);
        List<CommentResponse> content = comments.getContent().stream()
                .map(this::toCommentResponse)
                .toList();
        return PageResponse.of(content, page, size, comments.getTotalElements());
    }

    public CommentResponse updateComment(UUID commentId, UUID userId, CommentRequest request) {
        SubmissionComment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new ResourceNotFoundException("Comment", "id", commentId.toString()));
        if (!comment.getUserId().equals(userId)) {
            throw new BusinessException("UNAUTHORIZED", "You can only update your own comments");
        }
        comment.setContent(request.content());
        comment = commentRepository.save(comment);
        return toCommentResponse(comment);
    }

    public void deleteComment(UUID commentId, UUID userId) {
        SubmissionComment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new ResourceNotFoundException("Comment", "id", commentId.toString()));
        if (!comment.getUserId().equals(userId)) {
            throw new BusinessException("UNAUTHORIZED", "You can only delete your own comments");
        }
        comment.softDelete();
        commentRepository.save(comment);
    }

    private Submission findSubmission(UUID id) {
        return submissionRepository.findById(id)
                .filter(s -> !s.isDeleted())
                .orElseThrow(() -> new ResourceNotFoundException("Submission", "id", id.toString()));
    }

    private SubmissionResponse toResponse(Submission submission, UUID currentUserId) {
        SubmissionResponse response = new SubmissionResponse();
        response.setId(submission.getId());
        response.setAuthorId(submission.getAuthorId());
        response.setTitle(submission.getTitle());
        response.setDescription(submission.getDescription());
        response.setContentRating(submission.getContentRating());
        response.setLikeCount(likeRepository.countBySubmissionId(submission.getId()));
        response.setFavoriteCount(favoriteRepository.countBySubmissionId(submission.getId()));
        response.setCommentCount(commentRepository.countBySubmissionIdAndDeletedAtIsNull(submission.getId()));
        if (currentUserId != null) {
            response.setLiked(likeRepository.existsByUserIdAndSubmissionId(currentUserId, submission.getId()));
            response.setFavorited(favoriteRepository.existsByUserIdAndSubmissionId(currentUserId, submission.getId()));
        }
        response.setTags(getTags(submission.getId()));
        response.setCreatedAt(submission.getCreatedAt());
        response.setUpdatedAt(submission.getUpdatedAt());
        return response;
    }

    private TagResponse toTagResponse(SubmissionTag tag) {
        TagResponse response = new TagResponse();
        response.setId(tag.getId());
        response.setTagType(tag.getTagType());
        response.setTagName(tag.getTagName());
        return response;
    }

    private CommentResponse toCommentResponse(SubmissionComment comment) {
        CommentResponse response = new CommentResponse();
        response.setId(comment.getId());
        response.setUserId(comment.getUserId());
        response.setSubmissionId(comment.getSubmissionId());
        response.setContent(comment.getContent());
        response.setCreatedAt(comment.getCreatedAt());
        response.setUpdatedAt(comment.getUpdatedAt());
        return response;
    }
}

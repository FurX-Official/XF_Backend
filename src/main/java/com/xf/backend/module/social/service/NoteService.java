package com.xf.backend.module.social.service;

import com.xf.backend.common.exception.BusinessException;
import com.xf.backend.common.exception.ResourceNotFoundException;
import com.xf.backend.common.response.PageResponse;
import com.xf.backend.module.social.dto.NoteResponse;
import com.xf.backend.module.social.entity.Note;
import com.xf.backend.module.social.repository.NoteRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@Transactional
public class NoteService {

    private final NoteRepository noteRepository;

    public NoteService(NoteRepository noteRepository) {
        this.noteRepository = noteRepository;
    }

    public NoteResponse send(UUID senderId, UUID receiverId, String content) {
        if (senderId.equals(receiverId)) {
            throw new BusinessException("CANNOT_MESSAGE_SELF", "Cannot send a message to yourself");
        }
        Note note = new Note();
        note.setSenderId(senderId);
        note.setReceiverId(receiverId);
        note.setContent(content);
        note = noteRepository.save(note);
        return toResponse(note);
    }

    @Transactional(readOnly = true)
    public PageResponse<NoteResponse> getConversation(UUID user1, UUID user2, int page, int size) {
        Page<Note> notes = noteRepository.findConversation(user1, user2, PageRequest.of(page, size));
        return PageResponse.of(
                notes.getContent().stream().map(this::toResponse).toList(),
                page, size, notes.getTotalElements()
        );
    }

    @Transactional(readOnly = true)
    public long getUnreadCount(UUID userId) {
        return noteRepository.countByReceiverIdAndReadFalse(userId);
    }

    public void markAsRead(UUID noteId, UUID userId) {
        Note note = noteRepository.findById(noteId)
                .orElseThrow(() -> new ResourceNotFoundException("Note", "id", noteId.toString()));
        if (!note.getReceiverId().equals(userId)) {
            throw new BusinessException("NOT_AUTHORIZED", "You can only mark your own messages as read");
        }
        note.setRead(true);
        noteRepository.save(note);
    }

    private NoteResponse toResponse(Note note) {
        return new NoteResponse(
                note.getId(),
                note.getSenderId(),
                note.getReceiverId(),
                note.getContent(),
                note.isRead(),
                note.getCreatedAt()
        );
    }
}

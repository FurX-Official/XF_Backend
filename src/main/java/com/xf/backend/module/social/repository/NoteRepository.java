package com.xf.backend.module.social.repository;

import com.xf.backend.module.social.entity.Note;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface NoteRepository extends JpaRepository<Note, UUID> {

    @Query("SELECT n FROM Note n WHERE " +
           "((n.senderId = :user1 AND n.receiverId = :user2) OR (n.senderId = :user2 AND n.receiverId = :user1)) " +
           "AND n.deletedAt IS NULL ORDER BY n.createdAt DESC")
    Page<Note> findConversation(@Param("user1") UUID user1, @Param("user2") UUID user2, Pageable pageable);

    Page<Note> findByReceiverIdAndReadFalse(UUID receiverId, Pageable pageable);

    long countByReceiverIdAndReadFalse(UUID receiverId);
}

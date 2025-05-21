package com.websocket.ws.repository;

import com.websocket.ws.entities.ChatMessageEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ChatMessageRepository extends JpaRepository<ChatMessageEntity, Long> {
    List<ChatMessageEntity> findTop20ByConversationIdOrderBySentAtDesc(Long conversationId);
    
    Page<ChatMessageEntity> findByConversationIdOrderBySentAtDesc(Long conversationId, Pageable pageable);
    
    @Query("SELECT COUNT(m) FROM ChatMessageEntity m WHERE m.conversation.id = :conversationId AND m.sender != :userId AND m.readStatus = false")
    Long countUnreadMessages(Long conversationId, String userId);
    
    @Modifying
    @Query("UPDATE ChatMessageEntity m SET m.readStatus = true WHERE m.conversation.id = :conversationId AND m.sender != :userId AND m.readStatus = false")
    void markMessagesAsRead(Long conversationId, String userId);
    
    List<ChatMessageEntity> findByConversationIdOrderBySentAtAsc(Long conversationId);
}
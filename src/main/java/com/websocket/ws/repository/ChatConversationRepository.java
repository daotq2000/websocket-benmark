package com.websocket.ws.repository;

import com.websocket.ws.entities.ChatConversationEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ChatConversationRepository extends JpaRepository<ChatConversationEntity, Long> {

    @Query("SELECT c FROM ChatConversationEntity c WHERE " +
            "(c.user1 = :user1 AND c.user2 = :user2) OR " +
            "(c.user1 = :user2 AND c.user2 = :user1)")
    Optional<ChatConversationEntity> findByUsers(String user1, String user2);

    @Query("SELECT c FROM ChatConversationEntity c WHERE " +
            "c.user1 = :userId OR c.user2 = :userId " +
            "ORDER BY c.updatedAt DESC")
    List<ChatConversationEntity> findAllByUserId(String userId);
}
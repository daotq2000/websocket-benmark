package com.websocket.ws.repository;

import com.websocket.ws.entities.ChatConversationEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ChatConversationRepository extends JpaRepository<ChatConversationEntity, Long> {
    Optional<ChatConversationEntity> findByUser1AndUser2OrUser1AndUser2(String user1, String user2, String user3, String user4);

    List<ChatConversationEntity> findByUser1OrUser2OrderByLastActivityDesc(String user1, String user2);
}
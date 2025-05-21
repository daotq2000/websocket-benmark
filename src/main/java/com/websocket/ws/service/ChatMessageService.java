package com.websocket.ws.service;

import com.websocket.ws.domain.ChatMessage;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface ChatMessageService {
    ChatMessage sendMessage(ChatMessage message);

    List<ChatMessage> getRecentMessages(Long conversationId);

    Page<ChatMessage> getMessageHistory(Long conversationId, Pageable pageable);

    Long countUnreadMessages(Long conversationId, String userId);

    void markMessagesAsRead(Long conversationId, String userId);
}

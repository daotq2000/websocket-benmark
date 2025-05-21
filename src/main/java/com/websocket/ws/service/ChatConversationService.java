package com.websocket.ws.service;



import com.websocket.ws.domain.ChatConversation;

import java.util.List;

public interface ChatConversationService {

    ChatConversation getConversation(Long id, String userId);

    ChatConversation getOrCreateConversation(String user1, String user2);

    List<ChatConversation> getUserConversations(String userId);

    void markConversationAsRead(Long conversationId, String userId);

    void updateConversationLastActivity(Long conversationId);
}
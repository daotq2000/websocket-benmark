package com.websocket.ws.service;

import com.websocket.ws.domain.ChatConversation;
import com.websocket.ws.entities.ChatConversationEntity;
import com.websocket.ws.repository.ChatConversationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ChatConversationServiceImpl implements ChatConversationService {

    private final ChatConversationRepository conversationRepository;

    @Override
    @Transactional(readOnly = true)
    public List<ChatConversation> getUserConversations(String userId) {
        List<ChatConversationEntity> conversations = conversationRepository.findByUser1OrUser2OrderByLastActivityDesc(userId, userId);
        return conversations.stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public ChatConversation getConversation(Long id, String userId) {
        ChatConversationEntity conversation = conversationRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Conversation not found with id: " + id));

        if (!conversation.getUser1().equals(userId) && !conversation.getUser2().equals(userId)) {
            throw new RuntimeException("User not authorized to access this conversation");
        }

        return convertToDto(conversation);
    }

    @Override
    @Transactional
    public ChatConversation getOrCreateConversation(String user1, String user2) {
        return conversationRepository.findByUser1AndUser2OrUser1AndUser2(user1, user2, user2, user1)
                .map(this::convertToDto)
                .orElseGet(() -> createNewConversation(user1, user2));
    }

    @Override
    @Transactional
    public void markConversationAsRead(Long conversationId, String userId) {
        ChatConversationEntity conversation = conversationRepository.findById(conversationId)
                .orElseThrow(() -> new RuntimeException("Conversation not found with id: " + conversationId));

        if (!conversation.getUser1().equals(userId) && !conversation.getUser2().equals(userId)) {
            throw new RuntimeException("User not authorized to access this conversation");
        }

//        conversation.setLastActivity(Instant.now());
        conversationRepository.save(conversation);
    }

    @Override
    @Transactional
    public void updateConversationLastActivity(Long conversationId) {
        ChatConversationEntity conversation = conversationRepository.findById(conversationId)
                .orElseThrow(() -> new RuntimeException("Conversation not found with id: " + conversationId));

//        conversation.setLastActivity(Instant.now());
        conversationRepository.save(conversation);
    }

    private ChatConversation createNewConversation(String user1, String user2) {
        ChatConversationEntity conversation = ChatConversationEntity.builder()
                .user1(user1)
                .user2(user2)
                .createdAt(Instant.now())
                .lastActivity(Instant.now())
                .build();

        ChatConversationEntity savedConversation = conversationRepository.save(conversation);
        return convertToDto(savedConversation);
    }

    private ChatConversation convertToDto(ChatConversationEntity entity) {
        return ChatConversation.builder()
                .id(entity.getId())
                .user1(entity.getUser1())
                .user2(entity.getUser2())
                .createdAt(entity.getCreatedAt())
                .lastActivity(entity.getLastActivity())
                .build();
    }
}

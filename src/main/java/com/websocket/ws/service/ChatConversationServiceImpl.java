package com.websocket.ws.service;


import com.websocket.ws.domain.ChatConversation;
import com.websocket.ws.domain.ChatMessage;
import com.websocket.ws.entities.ChatConversationEntity;
import com.websocket.ws.entities.ChatMessageEntity;
import com.websocket.ws.repository.ChatConversationRepository;
import com.websocket.ws.repository.ChatMessageRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ChatConversationServiceImpl implements ChatConversationService {

    private final ChatConversationRepository conversationRepository;
    private final ChatMessageRepository messageRepository;
    private final RedisTemplate<String, Object> redisTemplate;
    private static final String CONVERSATION_CACHE_KEY = "conversation:";
    private static final Duration CACHE_TTL = Duration.ofMinutes(30);

    @Override
    @Transactional(readOnly = true)
    public ChatConversation getConversation(Long id, String userId) {
        String cacheKey = CONVERSATION_CACHE_KEY + id + ":" + userId;
        
        // Try to get from cache first
        ChatConversation cachedConversation = (ChatConversation) redisTemplate.opsForValue().get(cacheKey);
        if (cachedConversation != null) {
            return cachedConversation;
        }
        
        // If not in cache, get from database
        ChatConversationEntity conversation = conversationRepository.findById(id)
                .orElseThrow();
        
        // Convert to 
        ChatConversation conversationDomain = convertTo(conversation, userId);
        
        // Cache the result
        redisTemplate.opsForValue().set(cacheKey, conversation, CACHE_TTL);
        
        return conversationDomain;
    }

    @Override
    @Transactional
    public ChatConversation getOrCreateConversation(String user1, String user2) {
        Optional<ChatConversationEntity> existingConversation = conversationRepository.findByUsers(user1, user2);
        
        ChatConversationEntity conversation;
        if (existingConversation.isPresent()) {
            conversation = existingConversation.get();
        } else {
            conversation = ChatConversationEntity.builder()
                    .user1(user1)
                    .user2(user2)
                    .messages(new ArrayList<>())
                    .build();
            conversation = conversationRepository.save(conversation);
        }
        
        // Convert to  - viewing as user1
        return convertTo(conversation, user1);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ChatConversation> getUserConversations(String userId) {
        String cacheKey = "user:conversations:" + userId;
        
        // Try to get from cache
        List<ChatConversation> cachedConversations = (List<ChatConversation>) redisTemplate.opsForValue().get(cacheKey);
        if (cachedConversations != null) {
            return cachedConversations;
        }
        
        // If not in cache, fetch from database
        List<ChatConversationEntity> conversations = conversationRepository.findAllByUserId(userId);
        
        List<ChatConversation> conversationsDomain = conversations.stream()
                .map(conv -> convertTo(conv, userId))
                .toList();
        
        // Cache the results
        redisTemplate.opsForValue().set(cacheKey, conversations, CACHE_TTL);
        
        return conversationsDomain;
    }

    @Override
    @Transactional
    public void markConversationAsRead(Long conversationId, String userId) {
        messageRepository.markMessagesAsRead(conversationId, userId);
        
        // Invalidate cache for this conversation
        String cacheKey = CONVERSATION_CACHE_KEY + conversationId + ":" + userId;
        redisTemplate.delete(cacheKey);
        
        // Also invalidate user's conversations list cache
        redisTemplate.delete("user:conversations:" + userId);
    }

    @Override
    @Transactional
    public void updateConversationLastActivity(Long conversationId) {
        ChatConversationEntity conversation = conversationRepository.findById(conversationId)
                .orElseThrow(() -> new RuntimeException("Conversation not found with id: " + conversationId));
        
        // Save to update the updatedAt timestamp
        conversationRepository.save(conversation);
        
        // Invalidate cache for both users
        String user1 = conversation.getUser1();
        String user2 = conversation.getUser2();
        
        redisTemplate.delete(CONVERSATION_CACHE_KEY + conversationId + ":" + user1);
        redisTemplate.delete(CONVERSATION_CACHE_KEY + conversationId + ":" + user2);
        redisTemplate.delete("user:conversations:" + user1);
        redisTemplate.delete("user:conversations:" + user2);
    }

    private ChatConversation convertTo(ChatConversationEntity conversation, String currentUserId) {
        // Determine the other user in the conversation
        String otherUser = conversation.getUser1().equals(currentUserId) ? 
                           conversation.getUser2() : conversation.getUser1();
        
        // Get last message if available
        List<ChatMessageEntity> recentMessages = messageRepository
                .findTop20ByConversationIdOrderBySentAtDesc(conversation.getId());
        
        ChatMessage lastMessage = null;
        if (!recentMessages.isEmpty()) {
            var lastMessageEntity = recentMessages.get(0);
            lastMessage = ChatMessage.builder()
                    .id(lastMessageEntity.getId())
                    .conversationId(conversation.getId())
                    .sender(lastMessageEntity.getSender())
                    .content(lastMessageEntity.getContent())
                    .sentAt(lastMessageEntity.getSentAt())
                    .readStatus(lastMessageEntity.getReadStatus())
                    .build();
        }
        
        // Count unread messages
        Long unreadCount = messageRepository.countUnreadMessages(conversation.getId(), currentUserId);
        
        return ChatConversation.builder()
                .id(conversation.getId())
                .user1(conversation.getUser1())
                .user2(conversation.getUser2())
                .otherUserName(otherUser)
                .createdAt(conversation.getCreatedAt())
                .updatedAt(conversation.getUpdatedAt())
                .unreadCount(unreadCount)
                .lastMessage(lastMessage)
                .build();
    }
}

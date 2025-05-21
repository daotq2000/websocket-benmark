package com.websocket.ws.service;

import com.websocket.ws.domain.ChatMessage;
import com.websocket.ws.entities.ChatConversationEntity;
import com.websocket.ws.entities.ChatMessageEntity;
import com.websocket.ws.repository.ChatConversationRepository;
import com.websocket.ws.repository.ChatMessageRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ChatMessageServiceImpl implements ChatMessageService {

    private final ChatMessageRepository messageRepository;
    private final ChatConversationRepository conversationRepository;
    private final ChatConversationService conversationService;
    private final RedisTemplate<String, Object> redisTemplate;
    private final RedisTemplate<String, ChatMessage> messageRedisTemplate;
    private final ChannelTopic chatMessageTopic;
    private static final String MESSAGE_CACHE_KEY = "messages:conversation:";
    private static final Duration CACHE_TTL = Duration.ofMinutes(15);

    @Override
    @Transactional
    public ChatMessage sendMessage(ChatMessage messageDto) {
        // Get the conversation
        ChatConversationEntity conversation = conversationRepository.findById(messageDto.getConversationId())
                .orElseThrow(() -> new RuntimeException("Conversation not found with id: " + messageDto.getConversationId()));

        // Create and save message entity
        ChatMessageEntity message = ChatMessageEntity.builder()
                .conversation(conversation)
                .sender(messageDto.getSender())
                .content(messageDto.getContent())
                .sentAt(LocalDateTime.now())
                .readStatus(false)
                .build();

        ChatMessageEntity savedMessage = messageRepository.save(message);

        // Update the conversation's last activity timestamp
        conversationService.updateConversationLastActivity(conversation.getId());

        // Convert saved entity to DTO
        ChatMessage savedMessageDto = convertToDto(savedMessage);

        // Set recipient for WebSocket destination
        String recipient = conversation.getUser1().equals(messageDto.getSender())
                ? conversation.getUser2() : conversation.getUser1();
        savedMessageDto.setRecipientId(recipient);

        // Publish to Redis for WebSocket delivery
        publishMessage(savedMessageDto);

        // Invalidate cache
        invalidateMessageCache(conversation.getId());

        return savedMessageDto;
    }

    @Override
    @Transactional(readOnly = true)
    public List<ChatMessage> getRecentMessages(Long conversationId) {
        String cacheKey = MESSAGE_CACHE_KEY + conversationId + ":recent";

        // Try to get from cache
        List<ChatMessage> cachedMessages = (List<ChatMessage>) redisTemplate.opsForValue().get(cacheKey);
        if (cachedMessages != null) {
            return cachedMessages;
        }

        // If not in cache, get from database
        List<ChatMessageEntity> messages = messageRepository.findTop20ByConversationIdOrderBySentAtDesc(conversationId);

        List<ChatMessage> messageDtos = messages.stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());

        // Cache the results
        redisTemplate.opsForValue().set(cacheKey, messageDtos, CACHE_TTL);

        return messageDtos;
    }

    @Override
    @Transactional(readOnly = true)
    public  Page<ChatMessage> getMessageHistory(Long conversationId, Pageable pageable) {
        Page<ChatMessageEntity> messagePage = messageRepository.findByConversationIdOrderBySentAtDesc(conversationId, pageable);

        return messagePage.map(this::convertToDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Long countUnreadMessages(Long conversationId, String userId) {
        return messageRepository.countUnreadMessages(conversationId, userId);
    }

    @Override
    @Transactional
    public void markMessagesAsRead(Long conversationId, String userId) {
        messageRepository.markMessagesAsRead(conversationId, userId);

        // Invalidate cache
        invalidateMessageCache(conversationId);
    }

    @Async("messageThreadPoolTaskExecutor")
    protected void publishMessage(ChatMessage message) {
        messageRedisTemplate.convertAndSend(chatMessageTopic.getTopic(), message);
    }

    private void invalidateMessageCache(Long conversationId) {
        redisTemplate.delete(MESSAGE_CACHE_KEY + conversationId + ":recent");
    }

    private ChatMessage convertToDto(ChatMessageEntity message) {
        return ChatMessage.builder()
                .id(message.getId())
                .conversationId(message.getConversation().getId())
                .sender(message.getSender())
                .content(message.getContent())
                .sentAt(message.getSentAt())
                .readStatus(message.getReadStatus())
                .build();
    }
}
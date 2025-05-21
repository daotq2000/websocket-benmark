package com.websocket.ws.rest;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.ResponseEntity;
import java.util.Arrays;
import java.util.List;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;
import com.websocket.ws.service.ChatConversationService;
import com.websocket.ws.service.ChatMessageService;
import com.websocket.ws.domain.ChatConversation;
import com.websocket.ws.domain.ChatMessage;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Page;

public class ChatControllerTest {

    @Mock
    private ChatConversationService conversationService;

    @Mock
    private ChatMessageService messageService;

    @InjectMocks
    private ChatController chatController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void getUserConversations_ShouldReturnConversations() {
        // Arrange
        String userId = "user123";
        List<ChatConversation> expectedConversations = Arrays.asList(new ChatConversation(), new ChatConversation());
        when(conversationService.getUserConversations(userId)).thenReturn(expectedConversations);

        // Act
        ResponseEntity<List<ChatConversation>> response = chatController.getUserConversations(userId);

        // Assert
        assertEquals(expectedConversations, response.getBody());
    }

    @Test
    void getConversation_ShouldReturnConversation() {
        // Arrange
        Long id = 1L;
        String userId = "user123";
        ChatConversation expectedConversation = new ChatConversation();
        when(conversationService.getConversation(id, userId)).thenReturn(expectedConversation);

        // Act
        ResponseEntity<ChatConversation> response = chatController.getConversation(id, userId);

        // Assert
        assertEquals(expectedConversation, response.getBody());
    }

    @Test
    void createConversation_ShouldReturnConversation() {
        // Arrange
        String user1 = "user1";
        String user2 = "user2";
        ChatConversation expectedConversation = new ChatConversation();
        when(conversationService.getOrCreateConversation(user1, user2)).thenReturn(expectedConversation);

        // Act
        ResponseEntity<ChatConversation> response = chatController.createConversation(user1, user2);

        // Assert
        assertEquals(expectedConversation, response.getBody());
    }

    @Test
    void getRecentMessages_ShouldReturnMessages() {
        // Arrange
        Long conversationId = 1L;
        List<ChatMessage> expectedMessages = Arrays.asList(new ChatMessage(), new ChatMessage());
        when(messageService.getRecentMessages(conversationId)).thenReturn(expectedMessages);

        // Act
        ResponseEntity<List<ChatMessage>> response = chatController.getRecentMessages(conversationId);

        // Assert
        assertEquals(expectedMessages, response.getBody());
    }

    @Test
    void getMessageHistory_ShouldReturnPageOfMessages() {
        // Arrange
        Long conversationId = 1L;
        Pageable pageable = Pageable.unpaged();
        Page<ChatMessage> expectedPage = Page.empty();
        when(messageService.getMessageHistory(conversationId, pageable)).thenReturn(expectedPage);

        // Act
        ResponseEntity<Page<ChatMessage>> response = chatController.getMessageHistory(conversationId, pageable);

        // Assert
        assertEquals(expectedPage, response.getBody());
    }

    @Test
    void sendMessage_ShouldReturnMessage() {
        // Arrange
        ChatMessage message = new ChatMessage();
        when(messageService.sendMessage(message)).thenReturn(message);

        // Act
        ResponseEntity<ChatMessage> response = chatController.sendMessage(message);

        // Assert
        assertEquals(message, response.getBody());
    }

    @Test
    void markAsRead_ShouldReturnOk() {
        // Arrange
        Long conversationId = 1L;
        String userId = "user123";

        // Act
        ResponseEntity<Void> response = chatController.markAsRead(conversationId, userId);

        // Assert
        assertEquals(ResponseEntity.ok().build(), response);
    }

    @Test
    void getUnreadCount_ShouldReturnCount() {
        // Arrange
        Long conversationId = 1L;
        String userId = "user123";
        Long expectedCount = 5L;
        when(messageService.countUnreadMessages(conversationId, userId)).thenReturn(expectedCount);

        // Act
        ResponseEntity<Long> response = chatController.getUnreadCount(conversationId, userId);

        // Assert
        assertEquals(expectedCount, response.getBody());
    }
} 
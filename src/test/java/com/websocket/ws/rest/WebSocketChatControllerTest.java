package com.websocket.ws.rest;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import com.websocket.ws.domain.ChatMessage;
import com.websocket.ws.service.ChatMessageService;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class WebSocketChatControllerTest {

    @Mock
    private SimpMessagingTemplate messagingTemplate;

    @Mock
    private ChatMessageService chatMessageService;

    @InjectMocks
    private WebSocketChatController webSocketChatController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void sendMessage_ShouldSendMessageToRecipient() {
        // Arrange
        ChatMessage chatMessage = new ChatMessage();
        chatMessage.setRecipientId("recipient123");
        when(chatMessageService.sendMessage(chatMessage)).thenReturn(chatMessage);

        // Act
        webSocketChatController.sendMessage(chatMessage);

        // Assert
        verify(messagingTemplate).convertAndSendToUser("recipient123", "/queue/messages", chatMessage);
    }

    @Test
    void notifyTyping_ShouldSendTypingIndicator() {
        // Arrange
        ChatMessage chatMessage = new ChatMessage();
        chatMessage.setRecipientId("recipient123");

        // Act
        webSocketChatController.notifyTyping(chatMessage);

        // Assert
        verify(messagingTemplate).convertAndSendToUser("recipient123", "/queue/typing", chatMessage);
    }
} 
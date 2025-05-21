package com.websocket.ws.websocket;

import com.websocket.ws.domain.ChatMessage;
import com.websocket.ws.service.ChatMessageService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

@Controller
@RequiredArgsConstructor
@Slf4j
public class ChatMessageHandler {

    private final ChatMessageService messageService;
    private final SimpMessagingTemplate messagingTemplate;

    @MessageMapping("/chat.send")
    public void handleMessage(@Payload ChatMessage message, SimpMessageHeaderAccessor headerAccessor) {
        log.info("Received message: {}", message);
        
        try {
            // Save message to database
            ChatMessage savedMessage = messageService.sendMessage(message);
            
            // Send to specific user
            messagingTemplate.convertAndSendToUser(
                message.getRecipientId(),
                "/queue/messages",
                savedMessage
            );
            
            // Send to sender (for confirmation)
            messagingTemplate.convertAndSendToUser(
                message.getSender(),
                "/queue/messages",
                savedMessage
            );
            
            log.info("Message processed and sent: {}", savedMessage);
        } catch (Exception e) {
            log.error("Error processing message: {}", e.getMessage(), e);
            throw e;
        }
    }
} 
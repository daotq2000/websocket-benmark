package com.websocket.ws.rest;

import com.websocket.ws.domain.ChatMessage;
import com.websocket.ws.service.ChatMessageService;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

@Controller
@RequiredArgsConstructor
public class WebSocketChatController {

    private final SimpMessagingTemplate messagingTemplate;
    private final ChatMessageService chatMessageService;

    @MessageMapping("/chat.sendMessage")
    public void sendMessage(@Payload ChatMessage chatMessage) {
        // Save the message to database
        ChatMessage savedMessage = chatMessageService.sendMessage(chatMessage);

        // Send to the recipient via WebSocket
        String recipientId = savedMessage.getRecipientId();
        messagingTemplate.convertAndSendToUser(
                recipientId,
                "/queue/messages",
                savedMessage
        );
    }

    @MessageMapping("/chat.typing")
    public void notifyTyping(@Payload ChatMessage chatMessage) {
        String recipientId = chatMessage.getRecipientId();

        // Only send typing indicator, don't save to database
        messagingTemplate.convertAndSendToUser(
                recipientId,
                "/queue/typing",
                chatMessage
        );
    }
}
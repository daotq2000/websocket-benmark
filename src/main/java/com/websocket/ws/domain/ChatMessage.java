package com.websocket.ws.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ChatMessage {
    private Long id;
    private Long conversationId;
    private String sender;
    private String content;
    private LocalDateTime sentAt;
    private Boolean readStatus;

    // Used for WebSocket transmission
    private String recipientId;
}
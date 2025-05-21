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
public class ChatConversation {
    private Long id;
    private String user1;
    private String user2;
    private String otherUserName;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private Long unreadCount;
    private ChatMessage lastMessage;
}
package com.websocket.ws.entities;


import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "chat_conversation",
        indexes = {
                @Index(name = "idx_user_1", columnList = "user_1"),
                @Index(name = "idx_user_2", columnList = "user_2"),
                @Index(name = "idx_users", columnList = "user_1, user_2")
        })
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ChatConversationEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column(name = "user_1", nullable = false)
    private String user1;

    @Column(name = "user_2", nullable = false)
    private String user2;

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @OneToMany(mappedBy = "conversation", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<ChatMessageEntity> messages = new ArrayList<>();
}
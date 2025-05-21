package com.websocket.ws.rest;
import com.websocket.ws.domain.ChatConversation;
import com.websocket.ws.domain.ChatMessage;
import com.websocket.ws.service.ChatConversationService;
import com.websocket.ws.service.ChatMessageService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/chat")
@RequiredArgsConstructor
public class ChatController {

    private final ChatConversationService conversationService;
    private final ChatMessageService messageService;

    @GetMapping("/conversations")
    public ResponseEntity<List<ChatConversation>> getUserConversations(@RequestParam String userId) {
        return ResponseEntity.ok(conversationService.getUserConversations(userId));
    }

    @GetMapping("/conversation/{id}")
    public ResponseEntity<ChatConversation> getConversation(@PathVariable Long id,
                                                               @RequestParam String userId) {
        return ResponseEntity.ok(conversationService.getConversation(id, userId));
    }

    @PostMapping("/conversation")
    public ResponseEntity<ChatConversation> createConversation(@RequestParam String user1,
                                                                  @RequestParam String user2) {
        return ResponseEntity.ok(conversationService.getOrCreateConversation(user1, user2));
    }

    @GetMapping("/conversation/{conversationId}/messages")
    public ResponseEntity<List<ChatMessage>> getRecentMessages(@PathVariable Long conversationId) {
        return ResponseEntity.ok(messageService.getRecentMessages(conversationId));
    }

    @GetMapping("/conversation/{conversationId}/history")
    public ResponseEntity<Page<ChatMessage>> getMessageHistory(@PathVariable Long conversationId,
                                                                  Pageable pageable) {
        return ResponseEntity.ok(messageService.getMessageHistory(conversationId, pageable));
    }

    @PostMapping("/send")
    public ResponseEntity<ChatMessage> sendMessage(@RequestBody ChatMessage message) {
        return ResponseEntity.ok(messageService.sendMessage(message));
    }

    @PostMapping("/conversation/{conversationId}/read")
    public ResponseEntity<Void> markAsRead(@PathVariable Long conversationId,
                                           @RequestParam String userId) {
        conversationService.markConversationAsRead(conversationId, userId);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/conversation/{conversationId}/unread")
    public ResponseEntity<Long> getUnreadCount(@PathVariable Long conversationId,
                                               @RequestParam String userId) {
        return ResponseEntity.ok(messageService.countUnreadMessages(conversationId, userId));
    }
}
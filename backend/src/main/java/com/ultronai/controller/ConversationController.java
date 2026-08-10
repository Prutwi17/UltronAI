package com.ultronai.controller;

import com.ultronai.dto.request.CreateConversationRequest;
import com.ultronai.dto.request.SendMessageRequest;
import com.ultronai.dto.response.ConversationResponse;
import com.ultronai.dto.response.MessageResponse;
import com.ultronai.dto.response.PageResponse;
import com.ultronai.security.UserPrincipal;
import com.ultronai.service.ConversationService;
import com.ultronai.service.MessageService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/conversations")
public class ConversationController {

    private final ConversationService conversationService;
    private final MessageService messageService;

    public ConversationController(ConversationService conversationService, MessageService messageService) {
        this.conversationService = conversationService;
        this.messageService = messageService;
    }

    @PostMapping
    public ResponseEntity<ConversationResponse> createConversation(
        @Valid @RequestBody CreateConversationRequest request,
        @AuthenticationPrincipal UserPrincipal principal
    ) {
        ConversationResponse response = conversationService.createConversation(request, principal);
        
        // If initial message provided, save initial message
        if (request.getInitialMessage() != null && !request.getInitialMessage().trim().isEmpty()) {
            SendMessageRequest msgReq = new SendMessageRequest(response.getId(), request.getInitialMessage(), null, null);
            MessageResponse msgRes = messageService.sendMessage(msgReq, principal);
            response.setLastMessage(msgRes);
        }

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping
    public ResponseEntity<PageResponse<ConversationResponse>> listConversations(
        @AuthenticationPrincipal UserPrincipal principal,
        @RequestParam(defaultValue = "0") int page,
        @RequestParam(defaultValue = "20") int size
    ) {
        PageResponse<ConversationResponse> response = conversationService.listConversations(principal, page, size);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ConversationResponse> getConversation(
        @PathVariable Long id,
        @AuthenticationPrincipal UserPrincipal principal
    ) {
        ConversationResponse response = conversationService.getConversation(id, principal);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{id}/close")
    public ResponseEntity<ConversationResponse> closeConversation(
        @PathVariable Long id,
        @AuthenticationPrincipal UserPrincipal principal
    ) {
        ConversationResponse response = conversationService.closeConversation(id, principal);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}/messages")
    public ResponseEntity<PageResponse<MessageResponse>> getMessages(
        @PathVariable Long id,
        @AuthenticationPrincipal UserPrincipal principal,
        @RequestParam(defaultValue = "0") int page,
        @RequestParam(defaultValue = "50") int size
    ) {
        PageResponse<MessageResponse> response = messageService.getMessages(id, principal, page, size);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/{id}/messages")
    public ResponseEntity<MessageResponse> sendMessage(
        @PathVariable Long id,
        @Valid @RequestBody SendMessageRequest request,
        @AuthenticationPrincipal UserPrincipal principal
    ) {
        request.setConversationId(id);
        MessageResponse response = messageService.sendMessage(request, principal);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
}

package com.ultronai.controller;

import com.ultronai.dto.request.SendMessageRequest;
import com.ultronai.dto.response.MessageResponse;
import com.ultronai.security.UserPrincipal;
import com.ultronai.service.MessageService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Controller;

import java.security.Principal;

@Controller
public class ChatWebSocketController {

    private static final Logger logger = LoggerFactory.getLogger(ChatWebSocketController.class);
    private final MessageService messageService;

    public ChatWebSocketController(MessageService messageService) {
        this.messageService = messageService;
    }

    @MessageMapping("/chat.sendMessage")
    public MessageResponse sendMessage(@Payload SendMessageRequest request, Principal principal) {
        if (principal == null || !(principal instanceof UsernamePasswordAuthenticationToken auth)) {
            logger.warn("WebSocket message dropped: Unauthenticated principal");
            throw new IllegalStateException("Unauthenticated WebSocket session");
        }

        UserPrincipal userPrincipal = (UserPrincipal) auth.getPrincipal();
        logger.info("Processing WebSocket message for user {} in conversation {}", userPrincipal.getId(), request.getConversationId());
        return messageService.sendMessage(request, userPrincipal);
    }
}

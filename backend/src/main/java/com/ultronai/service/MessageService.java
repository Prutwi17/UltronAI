package com.ultronai.service;

import com.ultronai.dto.request.SendMessageRequest;
import com.ultronai.dto.response.MessageResponse;
import com.ultronai.dto.response.PageResponse;
import com.ultronai.model.entity.Conversation;
import com.ultronai.model.entity.Message;
import com.ultronai.repository.ConversationRepository;
import com.ultronai.repository.MessageRepository;
import com.ultronai.security.UserPrincipal;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class MessageService {

    private final MessageRepository messageRepository;
    private final ConversationRepository conversationRepository;
    private final ConversationService conversationService;
    private final SimpMessagingTemplate messagingTemplate;

    public MessageService(
        MessageRepository messageRepository,
        ConversationRepository conversationRepository,
        ConversationService conversationService,
        SimpMessagingTemplate messagingTemplate
    ) {
        this.messageRepository = messageRepository;
        this.conversationRepository = conversationRepository;
        this.conversationService = conversationService;
        this.messagingTemplate = messagingTemplate;
    }

    @Transactional
    public MessageResponse sendMessage(SendMessageRequest request, UserPrincipal principal) {
        Conversation conversation = conversationService.getAuthorizedConversation(request.getConversationId(), principal);

        Message message = new Message(
            conversation.getTenant(),
            conversation,
            request.getSenderType(),
            principal.getId(),
            request.getContent(),
            request.getContentType()
        );
        message = messageRepository.save(message);

        // Update conversation timestamp
        conversation.setUpdatedAt(LocalDateTime.now());
        conversationRepository.save(conversation);

        MessageResponse response = mapToMessageResponse(message);

        // Real-time broadcast over STOMP WebSocket to authorized tenant topic
        String destination = String.format("/topic/tenants/%d/conversations/%d", conversation.getTenant().getId(), conversation.getId());
        messagingTemplate.convertAndSend(destination, response);

        return response;
    }

    @Transactional(readOnly = true)
    public PageResponse<MessageResponse> getMessages(Long conversationId, UserPrincipal principal, int page, int size) {
        Conversation conversation = conversationService.getAuthorizedConversation(conversationId, principal);

        Pageable pageable = PageRequest.of(page, size);
        Page<Message> messagePage = messageRepository.findByTenantIdAndConversationIdOrderByCreatedAtAsc(
            conversation.getTenant().getId(),
            conversation.getId(),
            pageable
        );

        List<MessageResponse> content = messagePage.getContent().stream()
            .map(this::mapToMessageResponse)
            .collect(Collectors.toList());

        return new PageResponse<>(
            content,
            messagePage.getNumber(),
            messagePage.getSize(),
            messagePage.getTotalElements(),
            messagePage.getTotalPages(),
            messagePage.isLast()
        );
    }

    public MessageResponse mapToMessageResponse(Message message) {
        return new MessageResponse(
            message.getId(),
            message.getTenant().getId(),
            message.getConversation().getId(),
            message.getSenderType(),
            message.getSenderId(),
            message.getContent(),
            message.getContentType(),
            message.getCreatedAt()
        );
    }
}

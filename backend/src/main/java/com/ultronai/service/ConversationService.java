package com.ultronai.service;

import com.ultronai.dto.request.CreateConversationRequest;
import com.ultronai.dto.response.ConversationResponse;
import com.ultronai.dto.response.MessageResponse;
import com.ultronai.dto.response.PageResponse;
import com.ultronai.exception.ResourceNotFoundException;
import com.ultronai.exception.UnauthorizedException;
import com.ultronai.model.entity.Conversation;
import com.ultronai.model.entity.ConversationParticipant;
import com.ultronai.model.entity.Tenant;
import com.ultronai.model.entity.User;
import com.ultronai.model.enums.ConversationStatus;
import com.ultronai.model.enums.Role;
import com.ultronai.repository.ConversationParticipantRepository;
import com.ultronai.repository.ConversationRepository;
import com.ultronai.repository.TenantRepository;
import com.ultronai.repository.UserRepository;
import com.ultronai.security.UserPrincipal;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ConversationService {

    private final ConversationRepository conversationRepository;
    private final ConversationParticipantRepository participantRepository;
    private final UserRepository userRepository;
    private final TenantRepository tenantRepository;

    public ConversationService(
        ConversationRepository conversationRepository,
        ConversationParticipantRepository participantRepository,
        UserRepository userRepository,
        TenantRepository tenantRepository
    ) {
        this.conversationRepository = conversationRepository;
        this.participantRepository = participantRepository;
        this.userRepository = userRepository;
        this.tenantRepository = tenantRepository;
    }

    @Transactional
    public ConversationResponse createConversation(CreateConversationRequest request, UserPrincipal principal) {
        if (principal == null || principal.getTenantId() == null) {
            throw new UnauthorizedException("User or Tenant security context is missing");
        }

        Tenant tenant = tenantRepository.findById(principal.getTenantId())
            .orElseThrow(() -> new ResourceNotFoundException("Tenant not found"));

        User user = userRepository.findById(principal.getId())
            .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        Conversation conversation = new Conversation(tenant, user, request.getChannel());
        conversation = conversationRepository.save(conversation);

        // Add creator as participant
        ConversationParticipant participant = new ConversationParticipant(tenant, conversation, user);
        participantRepository.save(participant);

        return mapToConversationResponse(conversation, null);
    }

    @Transactional(readOnly = true)
    public PageResponse<ConversationResponse> listConversations(UserPrincipal principal, int page, int size) {
        if (principal == null || principal.getTenantId() == null) {
            throw new UnauthorizedException("User or Tenant security context is missing");
        }

        Pageable pageable = PageRequest.of(page, size, Sort.by("updatedAt").descending());
        Page<Conversation> conversationPage;

        // Support Agents and Admins see all tenant conversations; Customers see only their own
        if (principal.getRole() == Role.CUSTOMER) {
            conversationPage = conversationRepository.findByTenantIdAndUserId(principal.getTenantId(), principal.getId(), pageable);
        } else {
            conversationPage = conversationRepository.findByTenantId(principal.getTenantId(), pageable);
        }

        List<ConversationResponse> content = conversationPage.getContent().stream()
            .map(conv -> mapToConversationResponse(conv, null))
            .collect(Collectors.toList());

        return new PageResponse<>(
            content,
            conversationPage.getNumber(),
            conversationPage.getSize(),
            conversationPage.getTotalElements(),
            conversationPage.getTotalPages(),
            conversationPage.isLast()
        );
    }

    @Transactional(readOnly = true)
    public ConversationResponse getConversation(Long conversationId, UserPrincipal principal) {
        Conversation conversation = getAuthorizedConversation(conversationId, principal);
        return mapToConversationResponse(conversation, null);
    }

    @Transactional
    public ConversationResponse closeConversation(Long conversationId, UserPrincipal principal) {
        Conversation conversation = getAuthorizedConversation(conversationId, principal);
        conversation.setStatus(ConversationStatus.CLOSED);
        conversation = conversationRepository.save(conversation);
        return mapToConversationResponse(conversation, null);
    }

    public Conversation getAuthorizedConversation(Long conversationId, UserPrincipal principal) {
        if (principal == null || principal.getTenantId() == null) {
            throw new UnauthorizedException("Authentication context missing");
        }

        Conversation conversation = conversationRepository.findByIdAndTenantId(conversationId, principal.getTenantId())
            .orElseThrow(() -> new ResourceNotFoundException("Conversation not found for this tenant"));

        // Customers can only access their own conversations
        if (principal.getRole() == Role.CUSTOMER && !conversation.getUser().getId().equals(principal.getId())) {
            throw new UnauthorizedException("Access denied: You are not authorized to view this conversation");
        }

        return conversation;
    }

    public ConversationResponse mapToConversationResponse(Conversation conversation, MessageResponse lastMessage) {
        return new ConversationResponse(
            conversation.getId(),
            conversation.getTenant().getId(),
            conversation.getUser().getId(),
            conversation.getUser().getFullName(),
            conversation.getStatus(),
            conversation.getChannel(),
            lastMessage,
            conversation.getCreatedAt(),
            conversation.getUpdatedAt()
        );
    }
}

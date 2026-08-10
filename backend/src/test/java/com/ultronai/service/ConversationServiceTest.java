package com.ultronai.service;

import com.ultronai.dto.request.CreateConversationRequest;
import com.ultronai.dto.response.ConversationResponse;
import com.ultronai.dto.response.PageResponse;
import com.ultronai.exception.UnauthorizedException;
import com.ultronai.model.entity.Conversation;
import com.ultronai.model.entity.Tenant;
import com.ultronai.model.entity.User;
import com.ultronai.model.enums.ConversationStatus;
import com.ultronai.model.enums.Role;
import com.ultronai.repository.ConversationParticipantRepository;
import com.ultronai.repository.ConversationRepository;
import com.ultronai.repository.TenantRepository;
import com.ultronai.repository.UserRepository;
import com.ultronai.security.UserPrincipal;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.util.Collections;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ConversationServiceTest {

    @Mock
    private ConversationRepository conversationRepository;

    @Mock
    private ConversationParticipantRepository participantRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private TenantRepository tenantRepository;

    @InjectMocks
    private ConversationService conversationService;

    private Tenant testTenant;
    private User testUser;
    private Conversation testConversation;
    private UserPrincipal adminPrincipal;
    private UserPrincipal customerPrincipal;

    @BeforeEach
    void setUp() {
        testTenant = new Tenant("Acme Corp", "acme");
        testTenant.setId(1L);

        testUser = new User(testTenant, "Customer User", "cust@acme.com", "hash", Role.CUSTOMER);
        testUser.setId(10L);

        testConversation = new Conversation(testTenant, testUser, "WEB");
        testConversation.setId(100L);

        adminPrincipal = new UserPrincipal(1L, 1L, "Admin", "admin@acme.com", "hash", Role.TENANT_ADMIN, true);
        customerPrincipal = new UserPrincipal(10L, 1L, "Customer User", "cust@acme.com", "hash", Role.CUSTOMER, true);
    }

    @Test
    void testCreateConversationSuccess() {
        CreateConversationRequest request = new CreateConversationRequest("WEB", "Hello support!");

        when(tenantRepository.findById(1L)).thenReturn(Optional.of(testTenant));
        when(userRepository.findById(10L)).thenReturn(Optional.of(testUser));
        when(conversationRepository.save(any(Conversation.class))).thenReturn(testConversation);

        ConversationResponse response = conversationService.createConversation(request, customerPrincipal);

        assertNotNull(response);
        assertEquals(100L, response.getId());
        assertEquals(ConversationStatus.ACTIVE, response.getStatus());
        verify(participantRepository).save(any());
    }

    @Test
    void testListConversationsForCustomer() {
        when(conversationRepository.findByTenantIdAndUserId(eq(1L), eq(10L), any(Pageable.class)))
            .thenReturn(new PageImpl<>(Collections.singletonList(testConversation)));

        PageResponse<ConversationResponse> response = conversationService.listConversations(customerPrincipal, 0, 10);

        assertNotNull(response);
        assertEquals(1, response.getContent().size());
        assertEquals(100L, response.getContent().get(0).getId());
    }

    @Test
    void testGetConversationUnauthorizedCustomerRejection() {
        UserOtherTenantOtherUser();
        UserPrincipal otherCustomer = new UserPrincipal(99L, 1L, "Other Customer", "other@acme.com", "hash", Role.CUSTOMER, true);

        when(conversationRepository.findByIdAndTenantId(100L, 1L)).thenReturn(Optional.of(testConversation));

        assertThrows(UnauthorizedException.class, () -> conversationService.getConversation(100L, otherCustomer));
    }

    @Test
    void testCloseConversation() {
        when(conversationRepository.findByIdAndTenantId(100L, 1L)).thenReturn(Optional.of(testConversation));
        when(conversationRepository.save(any(Conversation.class))).thenReturn(testConversation);

        ConversationResponse response = conversationService.closeConversation(100L, adminPrincipal);

        assertNotNull(response);
        assertEquals(ConversationStatus.CLOSED, response.getStatus());
    }

    private void UserOtherTenantOtherUser() {
    }
}

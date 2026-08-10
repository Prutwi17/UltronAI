package com.ultronai.service;

import com.ultronai.dto.request.SendMessageRequest;
import com.ultronai.dto.response.MessageResponse;
import com.ultronai.dto.response.PageResponse;
import com.ultronai.model.entity.Conversation;
import com.ultronai.model.entity.Message;
import com.ultronai.model.entity.Tenant;
import com.ultronai.model.entity.User;
import com.ultronai.model.enums.MessageType;
import com.ultronai.model.enums.Role;
import com.ultronai.model.enums.SenderType;
import com.ultronai.repository.ConversationRepository;
import com.ultronai.repository.MessageRepository;
import com.ultronai.security.UserPrincipal;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.messaging.simp.SimpMessagingTemplate;

import java.util.Collections;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MessageServiceTest {

    @Mock
    private MessageRepository messageRepository;

    @Mock
    private ConversationRepository conversationRepository;

    @Mock
    private ConversationService conversationService;

    @Mock
    private SimpMessagingTemplate messagingTemplate;

    @InjectMocks
    private MessageService messageService;

    private Tenant testTenant;
    private User testUser;
    private Conversation testConversation;
    private Message testMessage;
    private UserPrincipal customerPrincipal;

    @BeforeEach
    void setUp() {
        testTenant = new Tenant("Acme Corp", "acme");
        testTenant.setId(1L);

        testUser = new User(testTenant, "Customer", "cust@acme.com", "hash", Role.CUSTOMER);
        testUser.setId(10L);

        testConversation = new Conversation(testTenant, testUser, "WEB");
        testConversation.setId(100L);

        testMessage = new Message(testTenant, testConversation, SenderType.USER, 10L, "Hello world", MessageType.TEXT);
        testMessage.setId(500L);

        customerPrincipal = new UserPrincipal(10L, 1L, "Customer", "cust@acme.com", "hash", Role.CUSTOMER, true);
    }

    @Test
    void testSendMessageAndBroadcastSTOMP() {
        SendMessageRequest request = new SendMessageRequest(100L, "Hello world", SenderType.USER, MessageType.TEXT);

        when(conversationService.getAuthorizedConversation(100L, customerPrincipal)).thenReturn(testConversation);
        when(messageRepository.save(any(Message.class))).thenReturn(testMessage);

        MessageResponse response = messageService.sendMessage(request, customerPrincipal);

        assertNotNull(response);
        assertEquals(500L, response.getId());
        assertEquals("Hello world", response.getContent());

        verify(messagingTemplate).convertAndSend(eq("/topic/tenants/1/conversations/100"), any(MessageResponse.class));
        verify(conversationRepository).save(testConversation);
    }

    @Test
    void testGetMessagesPaginated() {
        when(conversationService.getAuthorizedConversation(100L, customerPrincipal)).thenReturn(testConversation);
        when(messageRepository.findByTenantIdAndConversationIdOrderByCreatedAtAsc(eq(1L), eq(100L), any(Pageable.class)))
            .thenReturn(new PageImpl<>(Collections.singletonList(testMessage)));

        PageResponse<MessageResponse> response = messageService.getMessages(100L, customerPrincipal, 0, 50);

        assertNotNull(response);
        assertEquals(1, response.getContent().size());
        assertEquals("Hello world", response.getContent().get(0).getContent());
    }
}

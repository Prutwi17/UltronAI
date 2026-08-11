package com.ultronai.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ultronai.dto.response.MessageResponse;
import com.ultronai.dto.response.NlpAnalysisResponse;
import com.ultronai.model.entity.Conversation;
import com.ultronai.model.entity.Message;
import com.ultronai.model.entity.MessageNlpResult;
import com.ultronai.model.entity.Tenant;
import com.ultronai.model.entity.User;
import com.ultronai.model.enums.MessageType;
import com.ultronai.model.enums.Role;
import com.ultronai.model.enums.SenderType;
import com.ultronai.repository.MessageNlpResultRepository;
import com.ultronai.repository.MessageRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.messaging.simp.SimpMessagingTemplate;

import java.util.Collections;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class NlpServiceTest {

    @Mock
    private AiServiceClient aiServiceClient;

    @Mock
    private MessageNlpResultRepository nlpResultRepository;

    @Mock
    private MessageRepository messageRepository;

    @Mock
    private SimpMessagingTemplate messagingTemplate;

    @Spy
    private ObjectMapper objectMapper = new ObjectMapper();

    @InjectMocks
    private NlpService nlpService;

    private Tenant testTenant;
    private User testUser;
    private Conversation testConversation;
    private Message userMessage;

    @BeforeEach
    void setUp() {
        testTenant = new Tenant("Acme", "acme");
        testTenant.setId(1L);

        testUser = new User(testTenant, "Customer", "cust@acme.com", "hash", Role.CUSTOMER);
        testUser.setId(10L);

        testConversation = new Conversation(testTenant, testUser, "WEB");
        testConversation.setId(100L);

        userMessage = new Message(testTenant, testConversation, SenderType.USER, 10L, "Track my order 12345", MessageType.TEXT);
        userMessage.setId(500L);
    }

    @Test
    void testProcessUserMessageHighConfidenceOrderTracking() {
        NlpAnalysisResponse.EntityInfo entity = new NlpAnalysisResponse.EntityInfo("order_id", "12345", 0.98);
        NlpAnalysisResponse response = new NlpAnalysisResponse(
            new NlpAnalysisResponse.IntentInfo("ORDER_TRACKING", 0.94),
            Collections.singletonList(entity),
            false
        );

        when(aiServiceClient.analyzeText("Track my order 12345")).thenReturn(response);
        when(nlpResultRepository.save(any(MessageNlpResult.class))).thenAnswer(i -> i.getArgument(0));
        when(messageRepository.save(any(Message.class))).thenAnswer(i -> {
            Message m = i.getArgument(0);
            m.setId(600L);
            return m;
        });

        MessageNlpResult result = nlpService.processUserMessage(userMessage);

        assertNotNull(result);
        assertEquals("ORDER_TRACKING", result.getIntentName());
        assertEquals(0.94, result.getConfidence());
        assertFalse(result.isFallback());

        verify(messageRepository).save(any(Message.class));
        verify(messagingTemplate).convertAndSend(eq("/topic/tenants/1/conversations/100"), any(MessageResponse.class));
    }
}

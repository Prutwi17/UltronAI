package com.ultronai.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ultronai.dto.request.CreateConversationRequest;
import com.ultronai.dto.request.SendMessageRequest;
import com.ultronai.dto.response.ConversationResponse;
import com.ultronai.dto.response.MessageResponse;
import com.ultronai.dto.response.PageResponse;
import com.ultronai.model.enums.ConversationStatus;
import com.ultronai.model.enums.MessageType;
import com.ultronai.model.enums.SenderType;
import com.ultronai.security.JwtAuthenticationFilter;
import com.ultronai.security.JwtTokenProvider;
import com.ultronai.service.ConversationService;
import com.ultronai.service.MessageService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.Collections;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ConversationController.class)
@AutoConfigureMockMvc(addFilters = false)
class ConversationControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private ConversationService conversationService;

    @MockBean
    private MessageService messageService;

    @MockBean
    private JwtTokenProvider jwtTokenProvider;

    @MockBean
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    @Test
    void testCreateConversationEndpoint() throws Exception {
        CreateConversationRequest request = new CreateConversationRequest("WEB", "Hello!");
        ConversationResponse response = new ConversationResponse(100L, 1L, 10L, "Customer", ConversationStatus.ACTIVE, "WEB", null, LocalDateTime.now(), LocalDateTime.now());

        when(conversationService.createConversation(any(), any())).thenReturn(response);

        mockMvc.perform(post("/api/v1/conversations")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.id").value(100L))
            .andExpect(jsonPath("$.status").value("ACTIVE"));
    }

    @Test
    void testGetMessagesEndpoint() throws Exception {
        MessageResponse msgRes = new MessageResponse(500L, 1L, 100L, SenderType.USER, 10L, "Hello world", MessageType.TEXT, LocalDateTime.now());
        PageResponse<MessageResponse> pageRes = new PageResponse<>(Collections.singletonList(msgRes), 0, 50, 1, 1, true);

        when(messageService.getMessages(eq(100L), any(), eq(0), eq(50))).thenReturn(pageRes);

        mockMvc.perform(get("/api/v1/conversations/100/messages?page=0&size=50"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.content[0].content").value("Hello world"));
    }

    @Test
    void testSendMessageEndpoint() throws Exception {
        SendMessageRequest request = new SendMessageRequest(100L, "New message", SenderType.USER, MessageType.TEXT);
        MessageResponse response = new MessageResponse(501L, 1L, 100L, SenderType.USER, 10L, "New message", MessageType.TEXT, LocalDateTime.now());

        when(messageService.sendMessage(any(), any())).thenReturn(response);

        mockMvc.perform(post("/api/v1/conversations/100/messages")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.content").value("New message"));
    }
}

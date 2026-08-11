package com.ultronai.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ultronai.dto.request.CreateWorkflowRequest;
import com.ultronai.dto.response.WorkflowResponse;
import com.ultronai.model.enums.Role;
import com.ultronai.model.enums.WorkflowStatus;
import com.ultronai.security.UserPrincipal;
import com.ultronai.service.WorkflowService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.MethodParameter;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class WorkflowControllerTest {

    private MockMvc mockMvc;

    @Mock
    private WorkflowService workflowService;

    @InjectMocks
    private WorkflowController workflowController;

    private ObjectMapper objectMapper = new ObjectMapper();
    private UserPrincipal adminPrincipal;
    private WorkflowResponse workflowResponse;

    @BeforeEach
    void setUp() {
        adminPrincipal = new UserPrincipal(10L, 1L, "Admin", "admin@acme.com", "hash", Role.TENANT_ADMIN, true);
        workflowResponse = new WorkflowResponse(
            100L, 1L, "Order Flow", "Desc", "ORDER_TRACKING",
            WorkflowStatus.DRAFT, 1, Collections.emptyList(), Collections.emptyList(),
            LocalDateTime.now(), LocalDateTime.now()
        );

        mockMvc = MockMvcBuilders.standaloneSetup(workflowController)
            .setCustomArgumentResolvers(new HandlerMethodArgumentResolver() {
                @Override
                public boolean supportsParameter(MethodParameter parameter) {
                    return parameter.getParameterType().equals(UserPrincipal.class);
                }

                @Override
                public Object resolveArgument(MethodParameter parameter, ModelAndViewContainer mavContainer, NativeWebRequest webRequest, WebDataBinderFactory binderFactory) {
                    return adminPrincipal;
                }
            })
            .build();
    }

    @Test
    void testListWorkflows() throws Exception {
        when(workflowService.listWorkflows(1L)).thenReturn(List.of(workflowResponse));

        mockMvc.perform(get("/api/v1/workflows"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$[0].id").value(100))
            .andExpect(jsonPath("$[0].name").value("Order Flow"));
    }

    @Test
    void testCreateWorkflow() throws Exception {
        CreateWorkflowRequest request = new CreateWorkflowRequest("Order Flow", "Desc", "ORDER_TRACKING");
        when(workflowService.createWorkflow(any(), eq(1L))).thenReturn(workflowResponse);

        mockMvc.perform(post("/api/v1/workflows")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.id").value(100))
            .andExpect(jsonPath("$.name").value("Order Flow"));
    }
}

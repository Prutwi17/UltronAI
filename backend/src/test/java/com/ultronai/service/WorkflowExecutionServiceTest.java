package com.ultronai.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ultronai.model.entity.*;
import com.ultronai.model.enums.*;
import com.ultronai.repository.*;
import com.ultronai.workflow.ExecutionContext;
import com.ultronai.workflow.handler.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.messaging.simp.SimpMessagingTemplate;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class WorkflowExecutionServiceTest {

    @Mock
    private WorkflowRepository workflowRepository;

    @Mock
    private WorkflowNodeRepository nodeRepository;

    @Mock
    private WorkflowEdgeRepository edgeRepository;

    @Mock
    private WorkflowExecutionRepository executionRepository;

    @Mock
    private ConversationRepository conversationRepository;

    @Mock
    private MessageRepository messageRepository;

    @Mock
    private SimpMessagingTemplate messagingTemplate;

    @Spy
    private ObjectMapper objectMapper = new ObjectMapper();

    private WorkflowExecutionService executionService;

    private Tenant testTenant;
    private User testUser;
    private Conversation testConversation;
    private Workflow testWorkflow;

    @BeforeEach
    void setUp() {
        testTenant = new Tenant("Acme", "acme");
        testTenant.setId(1L);

        testUser = new User(testTenant, "User", "u@acme.com", "pass", Role.CUSTOMER);
        testUser.setId(10L);

        testConversation = new Conversation(testTenant, testUser, "WEB");
        testConversation.setId(100L);

        testWorkflow = new Workflow(testTenant, "Order Flow", "Desc", "ORDER_TRACKING");
        testWorkflow.setId(50L);

        StartNodeHandler startHandler = new StartNodeHandler();
        MessageNodeHandler messageHandler = new MessageNodeHandler(messageRepository, conversationRepository, messagingTemplate, objectMapper);
        ConditionNodeHandler conditionHandler = new ConditionNodeHandler();
        EndNodeHandler endHandler = new EndNodeHandler();

        List<NodeExecutionHandler> handlers = List.of(startHandler, messageHandler, conditionHandler, endHandler);

        executionService = new WorkflowExecutionService(
            workflowRepository,
            nodeRepository,
            edgeRepository,
            executionRepository,
            conversationRepository,
            objectMapper,
            handlers
        );
    }

    @Test
    void testSuccessfulWorkflowExecution() {
        WorkflowNode startNode = new WorkflowNode(testTenant, testWorkflow, NodeType.START, "Start", null, 0.0, 0.0);
        startNode.setId(200L);

        WorkflowNode msgNode = new WorkflowNode(testTenant, testWorkflow, NodeType.MESSAGE, "Message", "{\"message\":\"Tracking order #${entities.order_id}\"}", 100.0, 0.0);
        msgNode.setId(201L);

        WorkflowNode endNode = new WorkflowNode(testTenant, testWorkflow, NodeType.END, "End", null, 200.0, 0.0);
        endNode.setId(202L);

        WorkflowEdge edge1 = new WorkflowEdge(testTenant, testWorkflow, startNode, msgNode, null, null);
        WorkflowEdge edge2 = new WorkflowEdge(testTenant, testWorkflow, msgNode, endNode, null, null);

        when(workflowRepository.findByIdAndTenantId(50L, 1L)).thenReturn(Optional.of(testWorkflow));
        when(conversationRepository.findByIdAndTenantId(100L, 1L)).thenReturn(Optional.of(testConversation));
        when(nodeRepository.findByTenantIdAndWorkflowIdAndNodeType(1L, 50L, NodeType.START)).thenReturn(Optional.of(startNode));

        when(edgeRepository.findByTenantIdAndWorkflowIdAndSourceNodeId(1L, 50L, 200L)).thenReturn(List.of(edge1));
        when(edgeRepository.findByTenantIdAndWorkflowIdAndSourceNodeId(1L, 50L, 201L)).thenReturn(List.of(edge2));
        when(edgeRepository.findByTenantIdAndWorkflowIdAndSourceNodeId(1L, 50L, 202L)).thenReturn(List.of());

        when(executionRepository.save(any(WorkflowExecution.class))).thenAnswer(i -> i.getArgument(0));
        when(messageRepository.save(any(Message.class))).thenAnswer(i -> {
            Message m = i.getArgument(0);
            m.setId(999L);
            return m;
        });

        ExecutionContext context = new ExecutionContext(1L, 10L, 100L, "ORDER_TRACKING", 0.95, Map.of("order_id", "12345"));

        WorkflowExecution execution = executionService.executeWorkflow(50L, 100L, context);

        assertNotNull(execution);
        assertEquals(ExecutionStatus.COMPLETED, execution.getStatus());
        verify(messageRepository).save(any(Message.class));
        verify(messagingTemplate).convertAndSend(eq("/topic/tenants/1/conversations/100"), (Object) any());
    }
}

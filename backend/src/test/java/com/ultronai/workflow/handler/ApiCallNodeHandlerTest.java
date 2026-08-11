package com.ultronai.workflow.handler;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ultronai.model.entity.Tenant;
import com.ultronai.model.entity.Workflow;
import com.ultronai.model.entity.WorkflowNode;
import com.ultronai.model.enums.NodeType;
import com.ultronai.security.SsrfProtectionGuard;
import com.ultronai.workflow.ExecutionContext;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doNothing;

@ExtendWith(MockitoExtension.class)
class ApiCallNodeHandlerTest {

    @Mock
    private SsrfProtectionGuard ssrfGuard;

    @Spy
    private ObjectMapper objectMapper = new ObjectMapper();

    private ApiCallNodeHandler apiCallHandler;
    private Tenant testTenant;
    private Workflow testWorkflow;

    @BeforeEach
    void setUp() {
        apiCallHandler = new ApiCallNodeHandler(ssrfGuard, objectMapper);
        testTenant = new Tenant("Acme", "acme");
        testTenant.setId(1L);
        testWorkflow = new Workflow(testTenant, "Flow", "Desc", "ORDER_TRACKING");
    }

    @Test
    void testSupportedNodeType() {
        assertEquals(NodeType.API_CALL, apiCallHandler.getSupportedNodeType());
    }

    @Test
    void testExecuteApiCallNodeWithVariables() {
        doNothing().when(ssrfGuard).validateUrl(anyString());

        WorkflowNode apiNode = new WorkflowNode(
            testTenant,
            testWorkflow,
            NodeType.API_CALL,
            "Fetch Order",
            "{\"url\":\"https://httpbin.org/get?order_id=${entities.order_id}\",\"method\":\"GET\"}",
            100.0,
            0.0
        );
        apiNode.setId(300L);

        ExecutionContext context = new ExecutionContext(1L, 10L, 100L, "ORDER_TRACKING", 0.95, Map.of("order_id", "12345"));

        WorkflowNode nextNode = apiCallHandler.execute(apiNode, Collections.emptyList(), context);

        assertNull(nextNode);
        assertTrue(context.getVariables().containsKey("api_response"));
        Object apiRespObj = context.getVariables().get("api_response");
        assertTrue(apiRespObj instanceof Map);
        Map<?, ?> apiRespMap = (Map<?, ?>) apiRespObj;
        assertNotNull(apiRespMap.get("status"));
    }
}

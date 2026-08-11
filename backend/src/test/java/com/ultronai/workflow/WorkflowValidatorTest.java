package com.ultronai.workflow;

import com.ultronai.dto.response.WorkflowValidationResponse;
import com.ultronai.model.entity.Tenant;
import com.ultronai.model.entity.Workflow;
import com.ultronai.model.entity.WorkflowEdge;
import com.ultronai.model.entity.WorkflowNode;
import com.ultronai.model.enums.NodeType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class WorkflowValidatorTest {

    private WorkflowValidator validator;
    private Tenant testTenant;
    private Workflow testWorkflow;

    @BeforeEach
    void setUp() {
        validator = new WorkflowValidator();
        testTenant = new Tenant("Acme", "acme");
        testTenant.setId(1L);

        testWorkflow = new Workflow(testTenant, "Order Flow", "Desc", "ORDER_TRACKING");
        testWorkflow.setId(10L);
    }

    @Test
    void testValidWorkflowGraph() {
        WorkflowNode startNode = new WorkflowNode(testTenant, testWorkflow, NodeType.START, "Start", null, 0.0, 0.0);
        startNode.setId(100L);

        WorkflowNode msgNode = new WorkflowNode(testTenant, testWorkflow, NodeType.MESSAGE, "Send Msg", "{\"message\":\"Status\"}", 100.0, 0.0);
        msgNode.setId(101L);

        WorkflowNode endNode = new WorkflowNode(testTenant, testWorkflow, NodeType.END, "End", null, 200.0, 0.0);
        endNode.setId(102L);

        List<WorkflowNode> nodes = List.of(startNode, msgNode, endNode);

        WorkflowEdge edge1 = new WorkflowEdge(testTenant, testWorkflow, startNode, msgNode, null, null);
        edge1.setId(500L);
        WorkflowEdge edge2 = new WorkflowEdge(testTenant, testWorkflow, msgNode, endNode, null, null);
        edge2.setId(501L);

        List<WorkflowEdge> edges = List.of(edge1, edge2);

        WorkflowValidationResponse result = validator.validate(nodes, edges);

        assertTrue(result.isValid());
        assertTrue(result.getErrors().isEmpty());
    }

    @Test
    void testMissingStartNode() {
        WorkflowNode endNode = new WorkflowNode(testTenant, testWorkflow, NodeType.END, "End", null, 200.0, 0.0);
        endNode.setId(102L);

        WorkflowValidationResponse result = validator.validate(List.of(endNode), Collections.emptyList());

        assertFalse(result.isValid());
        assertTrue(result.getErrors().stream().anyMatch(e -> e.contains("exactly one START node")));
    }

    @Test
    void testUnreachableEndNode() {
        WorkflowNode startNode = new WorkflowNode(testTenant, testWorkflow, NodeType.START, "Start", null, 0.0, 0.0);
        startNode.setId(100L);

        WorkflowNode endNode = new WorkflowNode(testTenant, testWorkflow, NodeType.END, "End", null, 200.0, 0.0);
        endNode.setId(102L);

        // No edges connecting START to END
        WorkflowValidationResponse result = validator.validate(List.of(startNode, endNode), new ArrayList<>());

        assertFalse(result.isValid());
        assertTrue(result.getErrors().stream().anyMatch(e -> e.contains("No END node is reachable")));
    }
}

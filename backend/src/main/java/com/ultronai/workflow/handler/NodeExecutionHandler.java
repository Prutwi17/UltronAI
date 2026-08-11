package com.ultronai.workflow.handler;

import com.ultronai.model.entity.WorkflowEdge;
import com.ultronai.model.entity.WorkflowNode;
import com.ultronai.model.enums.NodeType;
import com.ultronai.workflow.ExecutionContext;

import java.util.List;

public interface NodeExecutionHandler {
    NodeType getSupportedNodeType();
    WorkflowNode execute(WorkflowNode node, List<WorkflowEdge> outgoingEdges, ExecutionContext context);
}

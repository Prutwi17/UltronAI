package com.ultronai.workflow.handler;

import com.ultronai.model.entity.WorkflowEdge;
import com.ultronai.model.entity.WorkflowNode;
import com.ultronai.model.enums.NodeType;
import com.ultronai.workflow.ExecutionContext;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class StartNodeHandler implements NodeExecutionHandler {

    @Override
    public NodeType getSupportedNodeType() {
        return NodeType.START;
    }

    @Override
    public WorkflowNode execute(WorkflowNode node, List<WorkflowEdge> outgoingEdges, ExecutionContext context) {
        if (outgoingEdges != null && !outgoingEdges.isEmpty()) {
            return outgoingEdges.get(0).getTargetNode();
        }
        return null;
    }
}

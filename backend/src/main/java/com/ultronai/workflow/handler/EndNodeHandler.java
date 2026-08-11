package com.ultronai.workflow.handler;

import com.ultronai.model.entity.WorkflowEdge;
import com.ultronai.model.entity.WorkflowNode;
import com.ultronai.model.enums.NodeType;
import com.ultronai.workflow.ExecutionContext;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class EndNodeHandler implements NodeExecutionHandler {

    @Override
    public NodeType getSupportedNodeType() {
        return NodeType.END;
    }

    @Override
    public WorkflowNode execute(WorkflowNode node, List<WorkflowEdge> outgoingEdges, ExecutionContext context) {
        // Return null to signify workflow termination
        return null;
    }
}

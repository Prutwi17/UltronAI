package com.ultronai.workflow.handler;

import com.ultronai.model.entity.WorkflowEdge;
import com.ultronai.model.entity.WorkflowNode;
import com.ultronai.model.enums.NodeType;
import com.ultronai.workflow.ExecutionContext;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class ConditionNodeHandler implements NodeExecutionHandler {

    @Override
    public NodeType getSupportedNodeType() {
        return NodeType.CONDITION;
    }

    @Override
    public WorkflowNode execute(WorkflowNode node, List<WorkflowEdge> outgoingEdges, ExecutionContext context) {
        if (outgoingEdges == null || outgoingEdges.isEmpty()) {
            return null;
        }

        for (WorkflowEdge edge : outgoingEdges) {
            String expr = edge.getConditionExpression();
            if (expr == null || expr.isBlank() || evaluateCondition(expr, context)) {
                return edge.getTargetNode();
            }
        }

        // Fallback to first target node
        return outgoingEdges.get(0).getTargetNode();
    }

    private boolean evaluateCondition(String expression, ExecutionContext context) {
        String expr = expression.trim();

        // 1. confidence >= 0.80
        if (expr.startsWith("confidence >=")) {
            try {
                double val = Double.parseDouble(expr.replace("confidence >=", "").trim());
                return context.getConfidence() != null && context.getConfidence() >= val;
            } catch (Exception ignored) {}
        }
        // 2. confidence < 0.60
        if (expr.startsWith("confidence <")) {
            try {
                double val = Double.parseDouble(expr.replace("confidence <", "").trim());
                return context.getConfidence() != null && context.getConfidence() < val;
            } catch (Exception ignored) {}
        }
        // 3. entity check: e.g. order_id != null
        if (expr.contains("!= null")) {
            String varName = expr.replace("!= null", "").replace("entities.", "").trim();
            return context.getEntities().containsKey(varName) && context.getEntities().get(varName) != null;
        }
        // 4. entity check: e.g. order_id == null
        if (expr.contains("== null")) {
            String varName = expr.replace("== null", "").replace("entities.", "").trim();
            return !context.getEntities().containsKey(varName) || context.getEntities().get(varName) == null;
        }
        // 5. intent check: e.g. intent == 'ORDER_TRACKING'
        if (expr.startsWith("intent ==")) {
            String targetIntent = expr.replace("intent ==", "").replace("'", "").replace("\"", "").trim();
            return targetIntent.equalsIgnoreCase(context.getIntentName());
        }

        // 6. api_response status check: e.g. api_response.status == 200
        if (expr.startsWith("api_response.status ==")) {
            try {
                int targetStatus = Integer.parseInt(expr.replace("api_response.status ==", "").trim());
                Object apiResp = context.getVariables().get("api_response");
                if (apiResp instanceof java.util.Map) {
                    Object statusObj = ((java.util.Map<?, ?>) apiResp).get("status");
                    return statusObj instanceof Integer && ((Integer) statusObj) == targetStatus;
                }
            } catch (Exception ignored) {}
        }
        if (expr.startsWith("api_response.status !=")) {
            try {
                int targetStatus = Integer.parseInt(expr.replace("api_response.status !=", "").trim());
                Object apiResp = context.getVariables().get("api_response");
                if (apiResp instanceof java.util.Map) {
                    Object statusObj = ((java.util.Map<?, ?>) apiResp).get("status");
                    return statusObj instanceof Integer && ((Integer) statusObj) != targetStatus;
                }
            } catch (Exception ignored) {}
        }

        return false;
    }
}

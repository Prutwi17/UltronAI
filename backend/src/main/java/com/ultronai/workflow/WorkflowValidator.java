package com.ultronai.workflow;

import com.ultronai.dto.response.WorkflowValidationResponse;
import com.ultronai.model.entity.WorkflowEdge;
import com.ultronai.model.entity.WorkflowNode;
import com.ultronai.model.enums.NodeType;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.stream.Collectors;

@Component
public class WorkflowValidator {

    public WorkflowValidationResponse validate(List<WorkflowNode> nodes, List<WorkflowEdge> edges) {
        List<String> errors = new ArrayList<>();

        if (nodes == null || nodes.isEmpty()) {
            errors.add("Workflow graph must contain at least one node.");
            return new WorkflowValidationResponse(false, errors);
        }

        // 1. Check START nodes
        List<WorkflowNode> startNodes = nodes.stream()
            .filter(n -> n.getNodeType() == NodeType.START)
            .collect(Collectors.toList());

        if (startNodes.isEmpty()) {
            errors.add("Workflow graph must contain exactly one START node (found 0).");
        } else if (startNodes.size() > 1) {
            errors.add("Workflow graph must contain exactly one START node (found " + startNodes.size() + ").");
        }

        // 2. Check END nodes
        List<WorkflowNode> endNodes = nodes.stream()
            .filter(n -> n.getNodeType() == NodeType.END)
            .collect(Collectors.toList());

        if (endNodes.isEmpty()) {
            errors.add("Workflow graph must contain at least one END node.");
        }

        // 3. Edge validation
        Set<Long> nodeIds = nodes.stream().map(WorkflowNode::getId).collect(Collectors.toSet());
        if (edges != null) {
            for (WorkflowEdge edge : edges) {
                if (!nodeIds.contains(edge.getSourceNode().getId())) {
                    errors.add("Edge #" + edge.getId() + " references invalid source node ID " + edge.getSourceNode().getId());
                }
                if (!nodeIds.contains(edge.getTargetNode().getId())) {
                    errors.add("Edge #" + edge.getId() + " references invalid target node ID " + edge.getTargetNode().getId());
                }
                if (!Objects.equals(edge.getWorkflow().getId(), nodes.get(0).getWorkflow().getId())) {
                    errors.add("Edge #" + edge.getId() + " belongs to a different workflow.");
                }
            }
        }

        // 4. Reachability from START node
        if (startNodes.size() == 1) {
            WorkflowNode startNode = startNodes.get(0);
            Set<Long> visitedNodeIds = new HashSet<>();
            Queue<Long> queue = new LinkedList<>();

            queue.add(startNode.getId());
            visitedNodeIds.add(startNode.getId());

            Map<Long, List<Long>> adjacencyMap = new HashMap<>();
            if (edges != null) {
                for (WorkflowEdge edge : edges) {
                    adjacencyMap.computeIfAbsent(edge.getSourceNode().getId(), k -> new ArrayList<>()).add(edge.getTargetNode().getId());
                }
            }

            while (!queue.isEmpty()) {
                Long currentId = queue.poll();
                List<Long> targets = adjacencyMap.getOrDefault(currentId, Collections.emptyList());
                for (Long targetId : targets) {
                    if (!visitedNodeIds.contains(targetId)) {
                        visitedNodeIds.add(targetId);
                        queue.add(targetId);
                    }
                }
            }

            // Check if any END node is reachable
            boolean endReachable = endNodes.stream().anyMatch(n -> visitedNodeIds.contains(n.getId()));
            if (!endReachable) {
                errors.add("No END node is reachable from the START node.");
            }
        }

        boolean isValid = errors.isEmpty();
        return new WorkflowValidationResponse(isValid, errors);
    }
}

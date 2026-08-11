package com.ultronai.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ultronai.model.entity.Conversation;
import com.ultronai.model.entity.Workflow;
import com.ultronai.model.entity.WorkflowEdge;
import com.ultronai.model.entity.WorkflowExecution;
import com.ultronai.model.entity.WorkflowNode;
import com.ultronai.model.enums.ExecutionStatus;
import com.ultronai.model.enums.NodeType;
import com.ultronai.repository.ConversationRepository;
import com.ultronai.repository.WorkflowEdgeRepository;
import com.ultronai.repository.WorkflowExecutionRepository;
import com.ultronai.repository.WorkflowNodeRepository;
import com.ultronai.repository.WorkflowRepository;
import com.ultronai.workflow.ExecutionContext;
import com.ultronai.workflow.handler.NodeExecutionHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class WorkflowExecutionService {

    private static final Logger logger = LoggerFactory.getLogger(WorkflowExecutionService.class);
    private static final int MAX_EXECUTION_STEPS = 50;

    private final WorkflowRepository workflowRepository;
    private final WorkflowNodeRepository nodeRepository;
    private final WorkflowEdgeRepository edgeRepository;
    private final WorkflowExecutionRepository executionRepository;
    private final ConversationRepository conversationRepository;
    private final ObjectMapper objectMapper;
    private final Map<NodeType, NodeExecutionHandler> handlerMap = new HashMap<>();

    public WorkflowExecutionService(
        WorkflowRepository workflowRepository,
        WorkflowNodeRepository nodeRepository,
        WorkflowEdgeRepository edgeRepository,
        WorkflowExecutionRepository executionRepository,
        ConversationRepository conversationRepository,
        ObjectMapper objectMapper,
        List<NodeExecutionHandler> handlers
    ) {
        this.workflowRepository = workflowRepository;
        this.nodeRepository = nodeRepository;
        this.edgeRepository = edgeRepository;
        this.executionRepository = executionRepository;
        this.conversationRepository = conversationRepository;
        this.objectMapper = objectMapper;
        for (NodeExecutionHandler handler : handlers) {
            handlerMap.put(handler.getSupportedNodeType(), handler);
        }
    }

    @Transactional
    public WorkflowExecution executeWorkflow(Long workflowId, Long conversationId, ExecutionContext context) {
        Workflow workflow = workflowRepository.findByIdAndTenantId(workflowId, context.getTenantId())
            .orElseThrow(() -> new IllegalArgumentException("Workflow not found"));

        Conversation conversation = conversationRepository.findByIdAndTenantId(conversationId, context.getTenantId())
            .orElseThrow(() -> new IllegalArgumentException("Conversation not found"));

        Optional<WorkflowNode> startNodeOpt = nodeRepository.findByTenantIdAndWorkflowIdAndNodeType(
            context.getTenantId(),
            workflow.getId(),
            NodeType.START
        );

        if (startNodeOpt.isEmpty()) {
            logger.error("Workflow #{} has no START node", workflowId);
            return null;
        }

        WorkflowNode currentNode = startNodeOpt.get();

        String contextJson = "{}";
        try {
            contextJson = objectMapper.writeValueAsString(context);
        } catch (Exception ignored) {}

        WorkflowExecution execution = new WorkflowExecution(
            workflow.getTenant(),
            workflow,
            conversation,
            currentNode,
            contextJson
        );
        execution = executionRepository.save(execution);

        int steps = 0;
        try {
            while (currentNode != null && steps < MAX_EXECUTION_STEPS) {
                steps++;
                execution.setCurrentNode(currentNode);

                NodeExecutionHandler handler = handlerMap.get(currentNode.getNodeType());
                if (handler == null) {
                    logger.warn("No handler found for node type {}", currentNode.getNodeType());
                    break;
                }

                List<WorkflowEdge> outgoingEdges = edgeRepository.findByTenantIdAndWorkflowIdAndSourceNodeId(
                    context.getTenantId(),
                    workflow.getId(),
                    currentNode.getId()
                );

                // Execute node handler
                currentNode = handler.execute(currentNode, outgoingEdges, context);
            }

            if (steps >= MAX_EXECUTION_STEPS) {
                logger.warn("Workflow #{} execution exceeded max step limit {}", workflowId, MAX_EXECUTION_STEPS);
                execution.setStatus(ExecutionStatus.FAILED);
            } else {
                execution.setStatus(ExecutionStatus.COMPLETED);
            }
        } catch (Exception ex) {
            logger.error("Error executing workflow #{}", workflowId, ex);
            execution.setStatus(ExecutionStatus.FAILED);
        } finally {
            execution.setCompletedAt(LocalDateTime.now());
            executionRepository.save(execution);
        }

        return execution;
    }
}

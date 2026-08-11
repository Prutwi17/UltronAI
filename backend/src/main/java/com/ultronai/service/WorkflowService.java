package com.ultronai.service;

import com.ultronai.dto.request.CreateWorkflowEdgeRequest;
import com.ultronai.dto.request.CreateWorkflowNodeRequest;
import com.ultronai.dto.request.CreateWorkflowRequest;
import com.ultronai.dto.response.WorkflowEdgeResponse;
import com.ultronai.dto.response.WorkflowNodeResponse;
import com.ultronai.dto.response.WorkflowResponse;
import com.ultronai.dto.response.WorkflowValidationResponse;
import com.ultronai.exception.ResourceNotFoundException;
import com.ultronai.model.entity.Tenant;
import com.ultronai.model.entity.Workflow;
import com.ultronai.model.entity.WorkflowEdge;
import com.ultronai.model.entity.WorkflowNode;
import com.ultronai.model.enums.WorkflowStatus;
import com.ultronai.repository.TenantRepository;
import com.ultronai.repository.WorkflowEdgeRepository;
import com.ultronai.repository.WorkflowNodeRepository;
import com.ultronai.repository.WorkflowRepository;
import com.ultronai.workflow.WorkflowValidator;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class WorkflowService {

    private final WorkflowRepository workflowRepository;
    private final WorkflowNodeRepository nodeRepository;
    private final WorkflowEdgeRepository edgeRepository;
    private final TenantRepository tenantRepository;
    private final WorkflowValidator workflowValidator;

    public WorkflowService(
        WorkflowRepository workflowRepository,
        WorkflowNodeRepository nodeRepository,
        WorkflowEdgeRepository edgeRepository,
        TenantRepository tenantRepository,
        WorkflowValidator workflowValidator
    ) {
        this.workflowRepository = workflowRepository;
        this.nodeRepository = nodeRepository;
        this.edgeRepository = edgeRepository;
        this.tenantRepository = tenantRepository;
        this.workflowValidator = workflowValidator;
    }

    @Transactional(readOnly = true)
    public List<WorkflowResponse> listWorkflows(Long tenantId) {
        return workflowRepository.findByTenantId(tenantId).stream()
            .map(this::mapToWorkflowResponse)
            .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public WorkflowResponse getWorkflow(Long id, Long tenantId) {
        Workflow workflow = workflowRepository.findByIdAndTenantId(id, tenantId)
            .orElseThrow(() -> new ResourceNotFoundException("Workflow not found"));
        return mapToWorkflowResponse(workflow);
    }

    @Transactional
    public WorkflowResponse createWorkflow(CreateWorkflowRequest request, Long tenantId) {
        Tenant tenant = tenantRepository.findById(tenantId)
            .orElseThrow(() -> new ResourceNotFoundException("Tenant not found"));

        Workflow workflow = new Workflow(tenant, request.getName(), request.getDescription(), request.getTriggerIntent());
        workflow = workflowRepository.save(workflow);

        return mapToWorkflowResponse(workflow);
    }

    @Transactional
    public WorkflowNodeResponse addNode(Long workflowId, CreateWorkflowNodeRequest request, Long tenantId) {
        Workflow workflow = workflowRepository.findByIdAndTenantId(workflowId, tenantId)
            .orElseThrow(() -> new ResourceNotFoundException("Workflow not found"));

        Tenant tenant = workflow.getTenant();
        WorkflowNode node = new WorkflowNode(
            tenant,
            workflow,
            request.getNodeType(),
            request.getName(),
            request.getConfigJson(),
            request.getPositionX(),
            request.getPositionY()
        );
        node = nodeRepository.save(node);

        return mapToNodeResponse(node);
    }

    @Transactional
    public WorkflowEdgeResponse addEdge(Long workflowId, CreateWorkflowEdgeRequest request, Long tenantId) {
        Workflow workflow = workflowRepository.findByIdAndTenantId(workflowId, tenantId)
            .orElseThrow(() -> new ResourceNotFoundException("Workflow not found"));

        Tenant tenant = workflow.getTenant();
        WorkflowNode sourceNode = nodeRepository.findByIdAndTenantId(request.getSourceNodeId(), tenantId)
            .orElseThrow(() -> new ResourceNotFoundException("Source node not found"));
        WorkflowNode targetNode = nodeRepository.findByIdAndTenantId(request.getTargetNodeId(), tenantId)
            .orElseThrow(() -> new ResourceNotFoundException("Target node not found"));

        WorkflowEdge edge = new WorkflowEdge(tenant, workflow, sourceNode, targetNode, request.getConditionExpression(), request.getLabel());
        edge = edgeRepository.save(edge);

        return mapToEdgeResponse(edge);
    }

    @Transactional
    public WorkflowValidationResponse validateWorkflow(Long id, Long tenantId) {
        Workflow workflow = workflowRepository.findByIdAndTenantId(id, tenantId)
            .orElseThrow(() -> new ResourceNotFoundException("Workflow not found"));

        List<WorkflowNode> nodes = nodeRepository.findByTenantIdAndWorkflowId(tenantId, id);
        List<WorkflowEdge> edges = edgeRepository.findByTenantIdAndWorkflowId(tenantId, id);

        return workflowValidator.validate(nodes, edges);
    }

    @Transactional
    public WorkflowResponse publishWorkflow(Long id, Long tenantId) {
        Workflow workflow = workflowRepository.findByIdAndTenantId(id, tenantId)
            .orElseThrow(() -> new ResourceNotFoundException("Workflow not found"));

        WorkflowValidationResponse validation = validateWorkflow(id, tenantId);
        if (!validation.isValid()) {
            throw new IllegalArgumentException("Cannot publish invalid workflow: " + String.join("; ", validation.getErrors()));
        }

        workflow.setStatus(WorkflowStatus.PUBLISHED);
        workflow.setVersion(workflow.getVersion() + 1);
        workflow = workflowRepository.save(workflow);

        return mapToWorkflowResponse(workflow);
    }

    private WorkflowResponse mapToWorkflowResponse(Workflow workflow) {
        List<WorkflowNodeResponse> nodes = nodeRepository.findByTenantIdAndWorkflowId(workflow.getTenant().getId(), workflow.getId())
            .stream().map(this::mapToNodeResponse).collect(Collectors.toList());

        List<WorkflowEdgeResponse> edges = edgeRepository.findByTenantIdAndWorkflowId(workflow.getTenant().getId(), workflow.getId())
            .stream().map(this::mapToEdgeResponse).collect(Collectors.toList());

        return new WorkflowResponse(
            workflow.getId(),
            workflow.getTenant().getId(),
            workflow.getName(),
            workflow.getDescription(),
            workflow.getTriggerIntent(),
            workflow.getStatus(),
            workflow.getVersion(),
            nodes,
            edges,
            workflow.getCreatedAt(),
            workflow.getUpdatedAt()
        );
    }

    private WorkflowNodeResponse mapToNodeResponse(WorkflowNode node) {
        return new WorkflowNodeResponse(
            node.getId(),
            node.getWorkflow().getId(),
            node.getNodeType(),
            node.getName(),
            node.getConfigJson(),
            node.getPositionX(),
            node.getPositionY(),
            node.getCreatedAt()
        );
    }

    private WorkflowEdgeResponse mapToEdgeResponse(WorkflowEdge edge) {
        return new WorkflowEdgeResponse(
            edge.getId(),
            edge.getWorkflow().getId(),
            edge.getSourceNode().getId(),
            edge.getTargetNode().getId(),
            edge.getConditionExpression(),
            edge.getLabel(),
            edge.getCreatedAt()
        );
    }
}

package com.ultronai.dto.response;

import com.ultronai.model.enums.WorkflowStatus;
import java.time.LocalDateTime;
import java.util.List;

public class WorkflowResponse {

    private Long id;
    private Long tenantId;
    private String name;
    private String description;
    private String triggerIntent;
    private WorkflowStatus status;
    private Integer version;
    private List<WorkflowNodeResponse> nodes;
    private List<WorkflowEdgeResponse> edges;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public WorkflowResponse() {
    }

    public WorkflowResponse(Long id, Long tenantId, String name, String description, String triggerIntent, WorkflowStatus status, Integer version, List<WorkflowNodeResponse> nodes, List<WorkflowEdgeResponse> edges, LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.id = id;
        this.tenantId = tenantId;
        this.name = name;
        this.description = description;
        this.triggerIntent = triggerIntent;
        this.status = status;
        this.version = version;
        this.nodes = nodes;
        this.edges = edges;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getTenantId() {
        return tenantId;
    }

    public void setTenantId(Long tenantId) {
        this.tenantId = tenantId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getTriggerIntent() {
        return triggerIntent;
    }

    public void setTriggerIntent(String triggerIntent) {
        this.triggerIntent = triggerIntent;
    }

    public WorkflowStatus getStatus() {
        return status;
    }

    public void setStatus(WorkflowStatus status) {
        this.status = status;
    }

    public Integer getVersion() {
        return version;
    }

    public void setVersion(Integer version) {
        this.version = version;
    }

    public List<WorkflowNodeResponse> getNodes() {
        return nodes;
    }

    public void setNodes(List<WorkflowNodeResponse> nodes) {
        this.nodes = nodes;
    }

    public List<WorkflowEdgeResponse> getEdges() {
        return edges;
    }

    public void setEdges(List<WorkflowEdgeResponse> edges) {
        this.edges = edges;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
}

package com.ultronai.dto.response;

import java.time.LocalDateTime;

public class WorkflowEdgeResponse {

    private Long id;
    private Long workflowId;
    private Long sourceNodeId;
    private Long targetNodeId;
    private String conditionExpression;
    private String label;
    private LocalDateTime createdAt;

    public WorkflowEdgeResponse() {
    }

    public WorkflowEdgeResponse(Long id, Long workflowId, Long sourceNodeId, Long targetNodeId, String conditionExpression, String label, LocalDateTime createdAt) {
        this.id = id;
        this.workflowId = workflowId;
        this.sourceNodeId = sourceNodeId;
        this.targetNodeId = targetNodeId;
        this.conditionExpression = conditionExpression;
        this.label = label;
        this.createdAt = createdAt;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getWorkflowId() {
        return workflowId;
    }

    public void setWorkflowId(Long workflowId) {
        this.workflowId = workflowId;
    }

    public Long getSourceNodeId() {
        return sourceNodeId;
    }

    public void setSourceNodeId(Long sourceNodeId) {
        this.sourceNodeId = sourceNodeId;
    }

    public Long getTargetNodeId() {
        return targetNodeId;
    }

    public void setTargetNodeId(Long targetNodeId) {
        this.targetNodeId = targetNodeId;
    }

    public String getConditionExpression() {
        return conditionExpression;
    }

    public void setConditionExpression(String conditionExpression) {
        this.conditionExpression = conditionExpression;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}

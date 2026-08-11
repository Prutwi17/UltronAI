package com.ultronai.dto.request;

import jakarta.validation.constraints.NotNull;

public class CreateWorkflowEdgeRequest {

    @NotNull(message = "Source node ID is required")
    private Long sourceNodeId;

    @NotNull(message = "Target node ID is required")
    private Long targetNodeId;

    private String conditionExpression;
    private String label;

    public CreateWorkflowEdgeRequest() {
    }

    public CreateWorkflowEdgeRequest(Long sourceNodeId, Long targetNodeId, String conditionExpression, String label) {
        this.sourceNodeId = sourceNodeId;
        this.targetNodeId = targetNodeId;
        this.conditionExpression = conditionExpression;
        this.label = label;
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
}

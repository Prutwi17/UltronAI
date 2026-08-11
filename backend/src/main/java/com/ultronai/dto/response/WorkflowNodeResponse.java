package com.ultronai.dto.response;

import com.ultronai.model.enums.NodeType;
import java.time.LocalDateTime;

public class WorkflowNodeResponse {

    private Long id;
    private Long workflowId;
    private NodeType nodeType;
    private String name;
    private String configJson;
    private Double positionX;
    private Double positionY;
    private LocalDateTime createdAt;

    public WorkflowNodeResponse() {
    }

    public WorkflowNodeResponse(Long id, Long workflowId, NodeType nodeType, String name, String configJson, Double positionX, Double positionY, LocalDateTime createdAt) {
        this.id = id;
        this.workflowId = workflowId;
        this.nodeType = nodeType;
        this.name = name;
        this.configJson = configJson;
        this.positionX = positionX;
        this.positionY = positionY;
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

    public NodeType getNodeType() {
        return nodeType;
    }

    public void setNodeType(NodeType nodeType) {
        this.nodeType = nodeType;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getConfigJson() {
        return configJson;
    }

    public void setConfigJson(String configJson) {
        this.configJson = configJson;
    }

    public Double getPositionX() {
        return positionX;
    }

    public void setPositionX(Double positionX) {
        this.positionX = positionX;
    }

    public Double getPositionY() {
        return positionY;
    }

    public void setPositionY(Double positionY) {
        this.positionY = positionY;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}

package com.ultronai.dto.request;

import com.ultronai.model.enums.NodeType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class CreateWorkflowNodeRequest {

    @NotNull(message = "Node type is required")
    private NodeType nodeType;

    @NotBlank(message = "Node name is required")
    private String name;

    private String configJson;
    private Double positionX = 0.0;
    private Double positionY = 0.0;

    public CreateWorkflowNodeRequest() {
    }

    public CreateWorkflowNodeRequest(NodeType nodeType, String name, String configJson, Double positionX, Double positionY) {
        this.nodeType = nodeType;
        this.name = name;
        this.configJson = configJson;
        this.positionX = positionX != null ? positionX : 0.0;
        this.positionY = positionY != null ? positionY : 0.0;
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
}

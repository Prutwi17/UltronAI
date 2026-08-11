package com.ultronai.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class CreateWorkflowRequest {

    @NotBlank(message = "Workflow name is required")
    @Size(max = 255, message = "Workflow name cannot exceed 255 characters")
    private String name;

    @Size(max = 500, message = "Description cannot exceed 500 characters")
    private String description;

    @Size(max = 100, message = "Trigger intent cannot exceed 100 characters")
    private String triggerIntent;

    public CreateWorkflowRequest() {
    }

    public CreateWorkflowRequest(String name, String description, String triggerIntent) {
        this.name = name;
        this.description = description;
        this.triggerIntent = triggerIntent;
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
}

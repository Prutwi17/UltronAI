package com.ultronai.workflow;

import java.util.HashMap;
import java.util.Map;

public class ExecutionContext {

    private Long tenantId;
    private Long userId;
    private Long conversationId;
    private String intentName;
    private Double confidence;
    private Map<String, String> entities = new HashMap<>();
    private Map<String, Object> variables = new HashMap<>();

    public ExecutionContext() {
    }

    public ExecutionContext(Long tenantId, Long userId, Long conversationId, String intentName, Double confidence, Map<String, String> entities) {
        this.tenantId = tenantId;
        this.userId = userId;
        this.conversationId = conversationId;
        this.intentName = intentName;
        this.confidence = confidence;
        if (entities != null) {
            this.entities.putAll(entities);
        }
    }

    public Long getTenantId() {
        return tenantId;
    }

    public void setTenantId(Long tenantId) {
        this.tenantId = tenantId;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public Long getConversationId() {
        return conversationId;
    }

    public void setConversationId(Long conversationId) {
        this.conversationId = conversationId;
    }

    public String getIntentName() {
        return intentName;
    }

    public void setIntentName(String intentName) {
        this.intentName = intentName;
    }

    public Double getConfidence() {
        return confidence;
    }

    public void setConfidence(Double confidence) {
        this.confidence = confidence;
    }

    public Map<String, String> getEntities() {
        return entities;
    }

    public void setEntities(Map<String, String> entities) {
        this.entities = entities;
    }

    public Map<String, Object> getVariables() {
        return variables;
    }

    public void setVariables(Map<String, Object> variables) {
        this.variables = variables;
    }
}

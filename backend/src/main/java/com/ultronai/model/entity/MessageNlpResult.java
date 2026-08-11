package com.ultronai.model.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "message_nlp_results", indexes = {
    @Index(name = "idx_nlp_results_tenant_id", columnList = "tenant_id"),
    @Index(name = "idx_nlp_results_message_id", columnList = "message_id", unique = true)
})
public class MessageNlpResult {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "tenant_id", nullable = false)
    private Tenant tenant;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "message_id", nullable = false)
    private Message message;

    @Column(name = "intent_name", nullable = false, length = 100)
    private String intentName;

    @Column(name = "confidence", nullable = false)
    private Double confidence;

    @Column(name = "entities_json", nullable = true, columnDefinition = "TEXT")
    private String entitiesJson;

    @Column(name = "fallback", nullable = false)
    private boolean fallback = false;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    public MessageNlpResult() {
    }

    public MessageNlpResult(Tenant tenant, Message message, String intentName, Double confidence, String entitiesJson, boolean fallback) {
        this.tenant = tenant;
        this.message = message;
        this.intentName = intentName;
        this.confidence = confidence;
        this.entitiesJson = entitiesJson;
        this.fallback = fallback;
    }

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Tenant getTenant() {
        return tenant;
    }

    public void setTenant(Tenant tenant) {
        this.tenant = tenant;
    }

    public Message getMessage() {
        return message;
    }

    public void setMessage(Message message) {
        this.message = message;
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

    public String getEntitiesJson() {
        return entitiesJson;
    }

    public void setEntitiesJson(String entitiesJson) {
        this.entitiesJson = entitiesJson;
    }

    public boolean isFallback() {
        return fallback;
    }

    public void setFallback(boolean fallback) {
        this.fallback = fallback;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}

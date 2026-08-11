package com.ultronai.model.entity;

import com.ultronai.model.enums.WorkflowStatus;
import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "workflows", indexes = {
    @Index(name = "idx_workflows_tenant_id", columnList = "tenant_id"),
    @Index(name = "idx_workflows_trigger_intent", columnList = "tenant_id, trigger_intent"),
    @Index(name = "idx_workflows_status", columnList = "status")
})
public class Workflow {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "tenant_id", nullable = false)
    private Tenant tenant;

    @Column(name = "name", nullable = false, length = 255)
    private String name;

    @Column(name = "description", nullable = true, length = 500)
    private String description;

    @Column(name = "trigger_intent", nullable = true, length = 100)
    private String triggerIntent;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 50)
    private WorkflowStatus status = WorkflowStatus.DRAFT;

    @Column(name = "version", nullable = false)
    private Integer version = 1;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    public Workflow() {
    }

    public Workflow(Tenant tenant, String name, String description, String triggerIntent) {
        this.tenant = tenant;
        this.name = name;
        this.description = description;
        this.triggerIntent = triggerIntent;
        this.status = WorkflowStatus.DRAFT;
        this.version = 1;
    }

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
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

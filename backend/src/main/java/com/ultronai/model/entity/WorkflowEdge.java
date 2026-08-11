package com.ultronai.model.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "workflow_edges", indexes = {
    @Index(name = "idx_edges_tenant_id", columnList = "tenant_id"),
    @Index(name = "idx_edges_workflow_id", columnList = "workflow_id"),
    @Index(name = "idx_edges_source_target", columnList = "source_node_id, target_node_id")
})
public class WorkflowEdge {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "tenant_id", nullable = false)
    private Tenant tenant;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "workflow_id", nullable = false)
    private Workflow workflow;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "source_node_id", nullable = false)
    private WorkflowNode sourceNode;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "target_node_id", nullable = false)
    private WorkflowNode targetNode;

    @Column(name = "condition_expression", nullable = true, length = 500)
    private String conditionExpression;

    @Column(name = "label", nullable = true, length = 100)
    private String label;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    public WorkflowEdge() {
    }

    public WorkflowEdge(Tenant tenant, Workflow workflow, WorkflowNode sourceNode, WorkflowNode targetNode, String conditionExpression, String label) {
        this.tenant = tenant;
        this.workflow = workflow;
        this.sourceNode = sourceNode;
        this.targetNode = targetNode;
        this.conditionExpression = conditionExpression;
        this.label = label;
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

    public Workflow getWorkflow() {
        return workflow;
    }

    public void setWorkflow(Workflow workflow) {
        this.workflow = workflow;
    }

    public WorkflowNode getSourceNode() {
        return sourceNode;
    }

    public void setSourceNode(WorkflowNode sourceNode) {
        this.sourceNode = sourceNode;
    }

    public WorkflowNode getTargetNode() {
        return targetNode;
    }

    public void setTargetNode(WorkflowNode targetNode) {
        this.targetNode = targetNode;
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

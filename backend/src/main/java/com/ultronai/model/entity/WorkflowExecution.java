package com.ultronai.model.entity;

import com.ultronai.model.enums.ExecutionStatus;
import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "workflow_executions", indexes = {
    @Index(name = "idx_executions_tenant_id", columnList = "tenant_id"),
    @Index(name = "idx_executions_workflow_id", columnList = "workflow_id"),
    @Index(name = "idx_executions_conversation_id", columnList = "conversation_id")
})
public class WorkflowExecution {

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
    @JoinColumn(name = "conversation_id", nullable = false)
    private Conversation conversation;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 50)
    private ExecutionStatus status = ExecutionStatus.RUNNING;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "current_node_id", nullable = true)
    private WorkflowNode currentNode;

    @Column(name = "context_json", nullable = true, columnDefinition = "TEXT")
    private String contextJson;

    @Column(name = "started_at", nullable = false, updatable = false)
    private LocalDateTime startedAt;

    @Column(name = "completed_at", nullable = true)
    private LocalDateTime completedAt;

    public WorkflowExecution() {
    }

    public WorkflowExecution(Tenant tenant, Workflow workflow, Conversation conversation, WorkflowNode currentNode, String contextJson) {
        this.tenant = tenant;
        this.workflow = workflow;
        this.conversation = conversation;
        this.currentNode = currentNode;
        this.contextJson = contextJson;
        this.status = ExecutionStatus.RUNNING;
    }

    @PrePersist
    protected void onCreate() {
        this.startedAt = LocalDateTime.now();
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

    public Conversation getConversation() {
        return conversation;
    }

    public void setConversation(Conversation conversation) {
        this.conversation = conversation;
    }

    public ExecutionStatus getStatus() {
        return status;
    }

    public void setStatus(ExecutionStatus status) {
        this.status = status;
    }

    public WorkflowNode getCurrentNode() {
        return currentNode;
    }

    public void setCurrentNode(WorkflowNode currentNode) {
        this.currentNode = currentNode;
    }

    public String getContextJson() {
        return contextJson;
    }

    public void setContextJson(String contextJson) {
        this.contextJson = contextJson;
    }

    public LocalDateTime getStartedAt() {
        return startedAt;
    }

    public void setStartedAt(LocalDateTime startedAt) {
        this.startedAt = startedAt;
    }

    public LocalDateTime getCompletedAt() {
        return completedAt;
    }

    public void setCompletedAt(LocalDateTime completedAt) {
        this.completedAt = completedAt;
    }
}

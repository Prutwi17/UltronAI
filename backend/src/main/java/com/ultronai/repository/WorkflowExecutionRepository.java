package com.ultronai.repository;

import com.ultronai.model.entity.WorkflowExecution;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface WorkflowExecutionRepository extends JpaRepository<WorkflowExecution, Long> {
    List<WorkflowExecution> findByTenantIdAndWorkflowId(Long tenantId, Long workflowId);
    List<WorkflowExecution> findByTenantIdAndConversationId(Long tenantId, Long conversationId);
}

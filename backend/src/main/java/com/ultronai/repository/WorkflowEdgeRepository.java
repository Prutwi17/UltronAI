package com.ultronai.repository;

import com.ultronai.model.entity.WorkflowEdge;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface WorkflowEdgeRepository extends JpaRepository<WorkflowEdge, Long> {
    List<WorkflowEdge> findByTenantIdAndWorkflowId(Long tenantId, Long workflowId);
    List<WorkflowEdge> findByTenantIdAndWorkflowIdAndSourceNodeId(Long tenantId, Long workflowId, Long sourceNodeId);
    Optional<WorkflowEdge> findByIdAndTenantId(Long id, Long tenantId);
}

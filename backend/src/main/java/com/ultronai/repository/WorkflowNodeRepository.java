package com.ultronai.repository;

import com.ultronai.model.entity.WorkflowNode;
import com.ultronai.model.enums.NodeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface WorkflowNodeRepository extends JpaRepository<WorkflowNode, Long> {
    List<WorkflowNode> findByTenantIdAndWorkflowId(Long tenantId, Long workflowId);
    Optional<WorkflowNode> findByIdAndTenantId(Long id, Long tenantId);
    Optional<WorkflowNode> findByTenantIdAndWorkflowIdAndNodeType(Long tenantId, Long workflowId, NodeType nodeType);
}

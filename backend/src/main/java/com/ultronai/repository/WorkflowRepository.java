package com.ultronai.repository;

import com.ultronai.model.entity.Workflow;
import com.ultronai.model.enums.WorkflowStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface WorkflowRepository extends JpaRepository<Workflow, Long> {
    List<Workflow> findByTenantId(Long tenantId);
    Optional<Workflow> findByIdAndTenantId(Long id, Long tenantId);
    Optional<Workflow> findByTenantIdAndTriggerIntentAndStatus(Long tenantId, String triggerIntent, WorkflowStatus status);
}

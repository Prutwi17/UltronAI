package com.ultronai.repository;

import com.ultronai.model.entity.IntentExample;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface IntentExampleRepository extends JpaRepository<IntentExample, Long> {
    List<IntentExample> findByTenantIdAndIntentId(Long tenantId, Long intentId);
    List<IntentExample> findByTenantId(Long tenantId);
}

package com.ultronai.repository;

import com.ultronai.model.entity.Intent;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface IntentRepository extends JpaRepository<Intent, Long> {
    List<Intent> findByTenantId(Long tenantId);
    Optional<Intent> findByIdAndTenantId(Long id, Long tenantId);
    Optional<Intent> findByTenantIdAndName(Long tenantId, String name);
    boolean existsByTenantIdAndName(Long tenantId, String name);
}

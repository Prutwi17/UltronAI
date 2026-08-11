package com.ultronai.repository;

import com.ultronai.model.entity.MessageNlpResult;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface MessageNlpResultRepository extends JpaRepository<MessageNlpResult, Long> {
    Optional<MessageNlpResult> findByTenantIdAndMessageId(Long tenantId, Long messageId);
}

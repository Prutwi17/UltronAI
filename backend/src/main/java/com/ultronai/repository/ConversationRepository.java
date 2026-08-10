package com.ultronai.repository;

import com.ultronai.model.entity.Conversation;
import com.ultronai.model.enums.ConversationStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ConversationRepository extends JpaRepository<Conversation, Long> {
    Page<Conversation> findByTenantId(Long tenantId, Pageable pageable);
    Page<Conversation> findByTenantIdAndUserId(Long tenantId, Long userId, Pageable pageable);
    Optional<Conversation> findByIdAndTenantId(Long id, Long tenantId);
    Page<Conversation> findByTenantIdAndStatus(Long tenantId, ConversationStatus status, Pageable pageable);
}

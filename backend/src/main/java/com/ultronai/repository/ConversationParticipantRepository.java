package com.ultronai.repository;

import com.ultronai.model.entity.ConversationParticipant;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ConversationParticipantRepository extends JpaRepository<ConversationParticipant, Long> {
    List<ConversationParticipant> findByTenantIdAndConversationId(Long tenantId, Long conversationId);
    Optional<ConversationParticipant> findByTenantIdAndConversationIdAndUserId(Long tenantId, Long conversationId, Long userId);
    boolean existsByTenantIdAndConversationIdAndUserId(Long tenantId, Long conversationId, Long userId);
}

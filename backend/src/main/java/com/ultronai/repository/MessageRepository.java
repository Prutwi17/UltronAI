package com.ultronai.repository;

import com.ultronai.model.entity.Message;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MessageRepository extends JpaRepository<Message, Long> {
    Page<Message> findByTenantIdAndConversationIdOrderByCreatedAtAsc(Long tenantId, Long conversationId, Pageable pageable);
    Page<Message> findByTenantIdAndConversationIdOrderByCreatedAtDesc(Long tenantId, Long conversationId, Pageable pageable);
}

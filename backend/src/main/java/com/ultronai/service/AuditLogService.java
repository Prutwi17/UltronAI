package com.ultronai.service;

import com.ultronai.model.entity.AuditLog;
import com.ultronai.repository.AuditLogRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AuditLogService {

    private static final Logger logger = LoggerFactory.getLogger(AuditLogService.class);
    private final AuditLogRepository auditLogRepository;

    public AuditLogService(AuditLogRepository auditLogRepository) {
        this.auditLogRepository = auditLogRepository;
    }

    @Transactional
    public void logSecurityEvent(Long tenantId, Long userId, String action, String resource, String resourceId, String ipAddress) {
        try {
            AuditLog logEntry = new AuditLog(tenantId, userId, action, resource, resourceId, ipAddress);
            auditLogRepository.save(logEntry);
            logger.info("SECURITY_AUDIT: action={} tenantId={} userId={} resource={}", action, tenantId, userId, resource);
        } catch (Exception e) {
            logger.error("Failed to record security audit log for action: " + action, e);
        }
    }
}

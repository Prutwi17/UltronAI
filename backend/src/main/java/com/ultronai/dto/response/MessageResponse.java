package com.ultronai.dto.response;

import com.ultronai.model.enums.MessageType;
import com.ultronai.model.enums.SenderType;
import java.time.LocalDateTime;

public class MessageResponse {

    private Long id;
    private Long tenantId;
    private Long conversationId;
    private SenderType senderType;
    private Long senderId;
    private String content;
    private MessageType contentType;
    private LocalDateTime createdAt;

    public MessageResponse() {
    }

    public MessageResponse(Long id, Long tenantId, Long conversationId, SenderType senderType, Long senderId, String content, MessageType contentType, LocalDateTime createdAt) {
        this.id = id;
        this.tenantId = tenantId;
        this.conversationId = conversationId;
        this.senderType = senderType;
        this.senderId = senderId;
        this.content = content;
        this.contentType = contentType;
        this.createdAt = createdAt;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getTenantId() {
        return tenantId;
    }

    public void setTenantId(Long tenantId) {
        this.tenantId = tenantId;
    }

    public Long getConversationId() {
        return conversationId;
    }

    public void setConversationId(Long conversationId) {
        this.conversationId = conversationId;
    }

    public SenderType getSenderType() {
        return senderType;
    }

    public void setSenderType(SenderType senderType) {
        this.senderType = senderType;
    }

    public Long getSenderId() {
        return senderId;
    }

    public void setSenderId(Long senderId) {
        this.senderId = senderId;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public MessageType getContentType() {
        return contentType;
    }

    public void setContentType(MessageType contentType) {
        this.contentType = contentType;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}

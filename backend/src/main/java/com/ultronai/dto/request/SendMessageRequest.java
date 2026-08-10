package com.ultronai.dto.request;

import com.ultronai.model.enums.MessageType;
import com.ultronai.model.enums.SenderType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public class SendMessageRequest {

    @NotNull(message = "Conversation ID is required")
    private Long conversationId;

    @NotBlank(message = "Content cannot be blank")
    @Size(max = 10000, message = "Content cannot exceed 10000 characters")
    private String content;

    private SenderType senderType = SenderType.USER;

    private MessageType contentType = MessageType.TEXT;

    public SendMessageRequest() {
    }

    public SendMessageRequest(Long conversationId, String content, SenderType senderType, MessageType contentType) {
        this.conversationId = conversationId;
        this.content = content;
        this.senderType = senderType != null ? senderType : SenderType.USER;
        this.contentType = contentType != null ? contentType : MessageType.TEXT;
    }

    public Long getConversationId() {
        return conversationId;
    }

    public void setConversationId(Long conversationId) {
        this.conversationId = conversationId;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public SenderType getSenderType() {
        return senderType;
    }

    public void setSenderType(SenderType senderType) {
        this.senderType = senderType;
    }

    public MessageType getContentType() {
        return contentType;
    }

    public void setContentType(MessageType contentType) {
        this.contentType = contentType;
    }
}

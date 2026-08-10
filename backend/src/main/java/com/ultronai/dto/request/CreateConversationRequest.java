package com.ultronai.dto.request;

import jakarta.validation.constraints.Size;

public class CreateConversationRequest {

    @Size(max = 50, message = "Channel name cannot exceed 50 characters")
    private String channel = "WEB";

    private String initialMessage;

    public CreateConversationRequest() {
    }

    public CreateConversationRequest(String channel, String initialMessage) {
        this.channel = channel != null ? channel : "WEB";
        this.initialMessage = initialMessage;
    }

    public String getChannel() {
        return channel;
    }

    public void setChannel(String channel) {
        this.channel = channel;
    }

    public String getInitialMessage() {
        return initialMessage;
    }

    public void setInitialMessage(String initialMessage) {
        this.initialMessage = initialMessage;
    }
}

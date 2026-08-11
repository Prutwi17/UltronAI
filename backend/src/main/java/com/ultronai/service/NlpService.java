package com.ultronai.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ultronai.dto.response.MessageResponse;
import com.ultronai.dto.response.NlpAnalysisResponse;
import com.ultronai.model.entity.Message;
import com.ultronai.model.entity.MessageNlpResult;
import com.ultronai.model.enums.MessageType;
import com.ultronai.model.enums.SenderType;
import com.ultronai.repository.MessageNlpResultRepository;
import com.ultronai.repository.MessageRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
public class NlpService {

    private static final Logger logger = LoggerFactory.getLogger(NlpService.class);

    private final AiServiceClient aiServiceClient;
    private final MessageNlpResultRepository nlpResultRepository;
    private final MessageRepository messageRepository;
    private final SimpMessagingTemplate messagingTemplate;
    private final ObjectMapper objectMapper;

    public NlpService(
        AiServiceClient aiServiceClient,
        MessageNlpResultRepository nlpResultRepository,
        MessageRepository messageRepository,
        SimpMessagingTemplate messagingTemplate,
        ObjectMapper objectMapper
    ) {
        this.aiServiceClient = aiServiceClient;
        this.nlpResultRepository = nlpResultRepository;
        this.messageRepository = messageRepository;
        this.messagingTemplate = messagingTemplate;
        this.objectMapper = objectMapper;
    }

    @Transactional
    public MessageNlpResult processUserMessage(Message userMessage) {
        NlpAnalysisResponse analysis = aiServiceClient.analyzeText(userMessage.getContent());

        String entitiesJson = "[]";
        try {
            entitiesJson = objectMapper.writeValueAsString(analysis.getEntities());
        } catch (JsonProcessingException e) {
            logger.error("Failed to serialize entities to JSON", e);
        }

        String intentName = analysis.getIntent() != null ? analysis.getIntent().getName() : "UNKNOWN";
        Double confidence = analysis.getIntent() != null ? analysis.getIntent().getConfidence() : 0.0;

        MessageNlpResult result = new MessageNlpResult(
            userMessage.getTenant(),
            userMessage,
            intentName,
            confidence,
            entitiesJson,
            analysis.isFallback()
        );
        result = nlpResultRepository.save(result);

        // Generate and broadcast assistant response
        generateAssistantResponse(userMessage, analysis);

        return result;
    }

    private void generateAssistantResponse(Message userMessage, NlpAnalysisResponse analysis) {
        String intent = analysis.getIntent() != null ? analysis.getIntent().getName() : "UNKNOWN";
        double confidence = analysis.getIntent() != null ? analysis.getIntent().getConfidence() : 0.0;

        String responseText;

        if (analysis.isFallback() || "UNKNOWN".equals(intent)) {
            responseText = "I'm sorry, I didn't quite catch that. Could you please rephrase your request?";
        } else {
            Optional<NlpAnalysisResponse.EntityInfo> orderEntity = analysis.getEntities().stream()
                .filter(e -> "order_id".equals(e.getType()))
                .findFirst();

            switch (intent) {
                case "ORDER_TRACKING":
                    if (orderEntity.isPresent()) {
                        responseText = String.format("I detected an order tracking request for order #%s (Intent: %s, Confidence: %.0f%%). Order status details are being fetched.", orderEntity.get().getValue(), intent, confidence * 100);
                    } else {
                        responseText = String.format("I detected an order tracking request (Intent: %s, Confidence: %.0f%%). Please provide your Order ID.", intent, confidence * 100);
                    }
                    break;
                case "CANCEL_ORDER":
                    if (orderEntity.isPresent()) {
                        responseText = String.format("I detected an order cancellation request for order #%s (Intent: %s, Confidence: %.0f%%). Order cancellation processing initiated.", orderEntity.get().getValue(), intent, confidence * 100);
                    } else {
                        responseText = String.format("I detected an order cancellation request (Intent: %s, Confidence: %.0f%%). Please provide the Order ID to cancel.", intent, confidence * 100);
                    }
                    break;
                case "GREETING":
                    responseText = "Hello! Welcome to UltronAI. How can I assist you today?";
                    break;
                case "ACCOUNT_HELP":
                    responseText = "I detected an account assistance request. Please visit your Account Settings or specify your account query.";
                    break;
                case "REFUND_REQUEST":
                    responseText = "I detected a refund request. Refund processing guidelines are available in Support.";
                    break;
                default:
                    responseText = String.format("Intent detected: %s (Confidence: %.0f%%). How else may I assist you?", intent, confidence * 100);
                    break;
            }
        }

        // Persist assistant message
        Message assistantMessage = new Message(
            userMessage.getTenant(),
            userMessage.getConversation(),
            SenderType.ASSISTANT,
            null,
            responseText,
            MessageType.TEXT
        );
        assistantMessage = messageRepository.save(assistantMessage);

        // Broadcast over STOMP WebSocket
        MessageResponse responseDto = new MessageResponse(
            assistantMessage.getId(),
            assistantMessage.getTenant().getId(),
            assistantMessage.getConversation().getId(),
            assistantMessage.getSenderType(),
            null,
            assistantMessage.getContent(),
            assistantMessage.getContentType(),
            assistantMessage.getCreatedAt()
        );

        String destination = String.format("/topic/tenants/%d/conversations/%d", userMessage.getTenant().getId(), userMessage.getConversation().getId());
        messagingTemplate.convertAndSend(destination, responseDto);
    }
}

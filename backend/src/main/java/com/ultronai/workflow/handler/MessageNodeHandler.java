package com.ultronai.workflow.handler;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ultronai.dto.response.MessageResponse;
import com.ultronai.model.entity.Conversation;
import com.ultronai.model.entity.Message;
import com.ultronai.model.entity.Tenant;
import com.ultronai.model.entity.WorkflowEdge;
import com.ultronai.model.entity.WorkflowNode;
import com.ultronai.model.enums.MessageType;
import com.ultronai.model.enums.NodeType;
import com.ultronai.model.enums.SenderType;
import com.ultronai.repository.ConversationRepository;
import com.ultronai.repository.MessageRepository;
import com.ultronai.workflow.ExecutionContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

@Component
public class MessageNodeHandler implements NodeExecutionHandler {

    private static final Logger logger = LoggerFactory.getLogger(MessageNodeHandler.class);

    private final MessageRepository messageRepository;
    private final ConversationRepository conversationRepository;
    private final SimpMessagingTemplate messagingTemplate;
    private final ObjectMapper objectMapper;

    public MessageNodeHandler(
        MessageRepository messageRepository,
        ConversationRepository conversationRepository,
        SimpMessagingTemplate messagingTemplate,
        ObjectMapper objectMapper
    ) {
        this.messageRepository = messageRepository;
        this.conversationRepository = conversationRepository;
        this.messagingTemplate = messagingTemplate;
        this.objectMapper = objectMapper;
    }

    @Override
    public NodeType getSupportedNodeType() {
        return NodeType.MESSAGE;
    }

    @Override
    public WorkflowNode execute(WorkflowNode node, List<WorkflowEdge> outgoingEdges, ExecutionContext context) {
        String templateText = "Workflow step executed.";
        if (node.getConfigJson() != null && !node.getConfigJson().isBlank()) {
            try {
                JsonNode jsonNode = objectMapper.readTree(node.getConfigJson());
                if (jsonNode.has("message")) {
                    templateText = jsonNode.get("message").asText();
                }
            } catch (Exception e) {
                logger.error("Failed to parse configJson for message node #{}", node.getId(), e);
            }
        }

        // Replace template variables ${intent}, ${confidence}, ${entities.key}
        String resolvedContent = resolveVariables(templateText, context);

        Tenant tenant = node.getTenant();
        Conversation conversation = conversationRepository.findByIdAndTenantId(context.getConversationId(), tenant.getId()).orElse(null);

        if (conversation != null) {
            Message assistantMessage = new Message(
                tenant,
                conversation,
                SenderType.ASSISTANT,
                null,
                resolvedContent,
                MessageType.TEXT
            );
            assistantMessage = messageRepository.save(assistantMessage);

            MessageResponse responseDto = new MessageResponse(
                assistantMessage.getId(),
                tenant.getId(),
                conversation.getId(),
                assistantMessage.getSenderType(),
                null,
                assistantMessage.getContent(),
                assistantMessage.getContentType(),
                assistantMessage.getCreatedAt()
            );

            String destination = String.format("/topic/tenants/%d/conversations/%d", tenant.getId(), conversation.getId());
            messagingTemplate.convertAndSend(destination, responseDto);
        }

        if (outgoingEdges != null && !outgoingEdges.isEmpty()) {
            return outgoingEdges.get(0).getTargetNode();
        }

        return null;
    }

    private String resolveVariables(String text, ExecutionContext context) {
        if (text == null) return "";
        String result = text;

        if (context.getIntentName() != null) {
            result = result.replace("${intent}", context.getIntentName());
        }
        if (context.getConfidence() != null) {
            result = result.replace("${confidence}", String.format("%.0f%%", context.getConfidence() * 100));
        }

        for (Map.Entry<String, String> entry : context.getEntities().entrySet()) {
            result = result.replace("${entities." + entry.getKey() + "}", entry.getValue());
            result = result.replace("${" + entry.getKey() + "}", entry.getValue());
        }

        return result;
    }
}

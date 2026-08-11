package com.ultronai.workflow.handler;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ultronai.model.entity.WorkflowEdge;
import com.ultronai.model.entity.WorkflowNode;
import com.ultronai.model.enums.NodeType;
import com.ultronai.security.SsrfProtectionGuard;
import com.ultronai.workflow.ExecutionContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.*;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class ApiCallNodeHandler implements NodeExecutionHandler {

    private static final Logger logger = LoggerFactory.getLogger(ApiCallNodeHandler.class);
    private static final int MAX_RESPONSE_BYTES = 1024 * 1024; // 1 MB

    private final SsrfProtectionGuard ssrfGuard;
    private final ObjectMapper objectMapper;
    private final RestTemplate restTemplate;

    public ApiCallNodeHandler(SsrfProtectionGuard ssrfGuard, ObjectMapper objectMapper) {
        this.ssrfGuard = ssrfGuard;
        this.objectMapper = objectMapper;

        SimpleClientHttpRequestFactory factory = new SimpleClientHttpRequestFactory();
        factory.setConnectTimeout(3000);
        factory.setReadTimeout(5000);
        this.restTemplate = new RestTemplate(factory);
    }

    @Override
    public NodeType getSupportedNodeType() {
        return NodeType.API_CALL;
    }

    @Override
    public WorkflowNode execute(WorkflowNode node, List<WorkflowEdge> outgoingEdges, ExecutionContext context) {
        if (node.getConfigJson() == null || node.getConfigJson().isBlank()) {
            logger.warn("API_CALL node #{} has no configJson", node.getId());
            return getNextNode(outgoingEdges);
        }

        try {
            JsonNode config = objectMapper.readTree(node.getConfigJson());
            String rawUrl = config.has("url") ? config.get("url").asText() : "";
            String methodStr = config.has("method") ? config.get("method").asText("GET").toUpperCase() : "GET";

            String resolvedUrl = resolveVariables(rawUrl, context);

            // Enforce SSRF validation
            ssrfGuard.validateUrl(resolvedUrl);

            HttpMethod httpMethod = HttpMethod.valueOf(methodStr);
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);

            if (config.has("headers") && config.get("headers").isObject()) {
                config.get("headers").fields().forEachRemaining(entry -> {
                    headers.add(entry.getKey(), resolveVariables(entry.getValue().asText(), context));
                });
            }

            String bodyPayload = null;
            if (config.has("body")) {
                JsonNode bodyNode = config.get("body");
                bodyPayload = bodyNode.isTextual() ? resolveVariables(bodyNode.asText(), context) : resolveVariables(objectMapper.writeValueAsString(bodyNode), context);
            }

            HttpEntity<String> entity = new HttpEntity<>(bodyPayload, headers);
            ResponseEntity<String> response = restTemplate.exchange(resolvedUrl, httpMethod, entity, String.class);

            String responseBody = response.getBody();
            if (responseBody != null && responseBody.length() > MAX_RESPONSE_BYTES) {
                responseBody = responseBody.substring(0, MAX_RESPONSE_BYTES);
            }

            Map<String, Object> apiResponseMap = new HashMap<>();
            apiResponseMap.put("status", response.getStatusCode().value());
            apiResponseMap.put("body_raw", responseBody);

            try {
                if (responseBody != null && (responseBody.trim().startsWith("{") || responseBody.trim().startsWith("["))) {
                    apiResponseMap.put("body", objectMapper.readValue(responseBody, Object.class));
                }
            } catch (Exception ignored) {}

            context.getVariables().put("api_response", apiResponseMap);
            logger.info("API_CALL node #{} completed with HTTP status {}", node.getId(), response.getStatusCode().value());

        } catch (Exception ex) {
            logger.error("API_CALL node #{} execution failed: {}", node.getId(), ex.getMessage());
            Map<String, Object> errorMap = new HashMap<>();
            errorMap.put("status", 500);
            errorMap.put("error", ex.getMessage());
            context.getVariables().put("api_response", errorMap);
        }

        return getNextNode(outgoingEdges);
    }

    private WorkflowNode getNextNode(List<WorkflowEdge> outgoingEdges) {
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
            result = result.replace("${confidence}", String.valueOf(context.getConfidence()));
        }

        for (Map.Entry<String, String> entry : context.getEntities().entrySet()) {
            result = result.replace("${entities." + entry.getKey() + "}", entry.getValue());
            result = result.replace("${" + entry.getKey() + "}", entry.getValue());
        }

        for (Map.Entry<String, Object> entry : context.getVariables().entrySet()) {
            result = result.replace("${variables." + entry.getKey() + "}", String.valueOf(entry.getValue()));
        }

        return result;
    }
}

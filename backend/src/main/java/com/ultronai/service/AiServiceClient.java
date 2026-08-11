package com.ultronai.service;

import com.ultronai.dto.request.NlpAnalysisRequest;
import com.ultronai.dto.response.NlpAnalysisResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Collections;

@Service
public class AiServiceClient {

    private static final Logger logger = LoggerFactory.getLogger(AiServiceClient.class);

    private final RestTemplate restTemplate;
    private final String aiServiceUrl;

    public AiServiceClient(
        @Value("${AI_SERVICE_URL:http://localhost:8000}") String aiServiceUrl
    ) {
        this.aiServiceUrl = aiServiceUrl;
        SimpleClientHttpRequestFactory factory = new SimpleClientHttpRequestFactory();
        factory.setConnectTimeout(3000);
        factory.setReadTimeout(3000);
        this.restTemplate = new RestTemplate(factory);
    }

    public NlpAnalysisResponse analyzeText(String text) {
        String endpoint = aiServiceUrl + "/api/v1/nlp/analyze";
        NlpAnalysisRequest request = new NlpAnalysisRequest(text);

        try {
            NlpAnalysisResponse response = restTemplate.postForObject(endpoint, request, NlpAnalysisResponse.class);
            if (response != null) {
                return response;
            }
        } catch (Exception ex) {
            logger.warn("AI Service call failed at endpoint {}: {}. Falling back to default UNKNOWN intent.", endpoint, ex.getMessage());
        }

        // Return safe fallback when AI service is offline or throws exception
        return new NlpAnalysisResponse(
            new NlpAnalysisResponse.IntentInfo("UNKNOWN", 0.0),
            Collections.emptyList(),
            true
        );
    }
}

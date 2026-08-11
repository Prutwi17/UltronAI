package com.ultronai.service;

import com.ultronai.dto.response.NlpAnalysisResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class AiServiceClientTest {

    private AiServiceClient aiServiceClient;

    @BeforeEach
    void setUp() {
        // Pointing to dummy port to test graceful offline fallback
        aiServiceClient = new AiServiceClient("http://localhost:9999");
    }

    @Test
    void testOfflineFallbackGracefulHandling() {
        NlpAnalysisResponse response = aiServiceClient.analyzeText("Track my order 12345");

        assertNotNull(response);
        assertTrue(response.isFallback());
        assertNotNull(response.getIntent());
        assertEquals("UNKNOWN", response.getIntent().getName());
        assertEquals(0.0, response.getIntent().getConfidence());
        assertTrue(response.getEntities().isEmpty());
    }
}

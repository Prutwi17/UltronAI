package com.ultronai.dto.response;

import java.util.List;

public class NlpAnalysisResponse {

    private IntentInfo intent;
    private List<EntityInfo> entities;
    private boolean fallback;

    public NlpAnalysisResponse() {
    }

    public NlpAnalysisResponse(IntentInfo intent, List<EntityInfo> entities, boolean fallback) {
        this.intent = intent;
        this.entities = entities;
        this.fallback = fallback;
    }

    public IntentInfo getIntent() {
        return intent;
    }

    public void setIntent(IntentInfo intent) {
        this.intent = intent;
    }

    public List<EntityInfo> getEntities() {
        return entities;
    }

    public void setEntities(List<EntityInfo> entities) {
        this.entities = entities;
    }

    public boolean isFallback() {
        return fallback;
    }

    public void setFallback(boolean fallback) {
        this.fallback = fallback;
    }

    public static class IntentInfo {
        private String name;
        private Double confidence;

        public IntentInfo() {
        }

        public IntentInfo(String name, Double confidence) {
            this.name = name;
            this.confidence = confidence;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public Double getConfidence() {
            return confidence;
        }

        public void setConfidence(Double confidence) {
            this.confidence = confidence;
        }
    }

    public static class EntityInfo {
        private String type;
        private String value;
        private Double confidence;

        public EntityInfo() {
        }

        public EntityInfo(String type, String value, Double confidence) {
            this.type = type;
            this.value = value;
            this.confidence = confidence;
        }

        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
        }

        public String getValue() {
            return value;
        }

        public void setValue(String value) {
            this.value = value;
        }

        public Double getConfidence() {
            return confidence;
        }

        public void setConfidence(Double confidence) {
            this.confidence = confidence;
        }
    }
}

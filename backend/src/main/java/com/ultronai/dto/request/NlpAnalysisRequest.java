package com.ultronai.dto.request;

public class NlpAnalysisRequest {

    private String text;

    public NlpAnalysisRequest() {
    }

    public NlpAnalysisRequest(String text) {
        this.text = text;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }
}

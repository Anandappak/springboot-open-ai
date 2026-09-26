package com.example.opensearchassistant.model;

import java.util.List;

public class AssistantResponse {
    private String answer;
    private List<SearchResult> results;

    public AssistantResponse() {
    }

    public AssistantResponse(String answer, List<SearchResult> results) {
        this.answer = answer;
        this.results = results;
    }

    public String getAnswer() {
        return answer;
    }

    public void setAnswer(String answer) {
        this.answer = answer;
    }

    public List<SearchResult> getResults() {
        return results;
    }

    public void setResults(List<SearchResult> results) {
        this.results = results;
    }
}

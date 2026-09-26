package com.example.opensearchassistant.model;

public class AssistantRequest {
    private String query;
    private int maxResults = 5;

    public AssistantRequest() {
    }

    public AssistantRequest(String query, int maxResults) {
        this.query = query;
        this.maxResults = maxResults;
    }

    public String getQuery() {
        return query;
    }

    public void setQuery(String query) {
        this.query = query;
    }

    public int getMaxResults() {
        return maxResults;
    }

    public void setMaxResults(int maxResults) {
        this.maxResults = maxResults;
    }
}

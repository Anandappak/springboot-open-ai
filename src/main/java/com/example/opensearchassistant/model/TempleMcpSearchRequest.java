package com.example.opensearchassistant.model;

import java.util.Map;

public record TempleMcpSearchRequest(String query, String entityType, Map<String, String> filters) {
}

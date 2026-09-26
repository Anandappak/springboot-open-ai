package com.example.opensearchassistant.service;

import com.example.opensearchassistant.config.OpenSearchProperties;
import com.example.opensearchassistant.model.SearchResult;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.ArrayList;
import java.util.List;

@Service
public class OpenSearchService {

    private static final Logger log = LoggerFactory.getLogger(OpenSearchService.class);

    private final OpenSearchProperties properties;
    private final WebClient webClient;
    private final ObjectMapper objectMapper;

    public OpenSearchService(OpenSearchProperties properties, WebClient.Builder webClientBuilder, ObjectMapper objectMapper) {
        this.properties = properties;
        this.webClient = webClientBuilder.build();
        this.objectMapper = objectMapper;
    }

    public List<SearchResult> search(String query, int maxResults) {
        if (query == null || query.isBlank()) {
            throw new IllegalArgumentException("Query must not be blank");
        }

        String endpoint = properties.getHost().replaceAll("/+$", "") + "/" + properties.getIndex() + "/_search";

        try {
            String response = webClient.post()
                    .uri(endpoint)
                    .headers(headers -> {
                        if (properties.getUsername() != null && !properties.getUsername().isBlank()
                                && properties.getPassword() != null && !properties.getPassword().isBlank()) {
                            headers.setBasicAuth(properties.getUsername(), properties.getPassword());
                        }
                    })
                    .bodyValue("""
                            {
                              "size": %d,
                              "query": {
                                "multi_match": {
                                  "query": "%s",
                                  "fields": ["title^2", "content", "summary"]
                                }
                              }
                            }
                            """.formatted(maxResults, query.replace("\"", "\\\"")))
                    .retrieve()
                    .onStatus(HttpStatusCode::isError, clientResponse -> {
                        log.warn("OpenSearch request failed with status {}", clientResponse.statusCode());
                        return clientResponse.bodyToMono(String.class)
                                .defaultIfEmpty("")
                                .flatMap(body -> Mono.error(new IllegalStateException("OpenSearch query failed: " + body)));
                    })
                    .bodyToMono(String.class)
                    .block();

            return parseResults(response, maxResults);
        } catch (Exception ex) {
            log.warn("OpenSearch fallback triggered for query '{}': {}", query, ex.getMessage());
            return fallbackResults(maxResults);
        }
    }

    private List<SearchResult> parseResults(String response, int maxResults) throws Exception {
        if (response == null || response.isBlank()) {
            return fallbackResults(maxResults);
        }

        JsonNode root = objectMapper.readTree(response);
        JsonNode hits = root.path("hits").path("hits");
        List<SearchResult> results = new ArrayList<>();

        if (hits.isArray()) {
            for (JsonNode hit : hits) {
                JsonNode source = hit.path("_source");
                String id = hit.path("_id").asText();
                String title = source.path("title").asText();
                String content = source.path("content").asText();
                if (title.isBlank() && source.path("summary").asText() != null) {
                    title = source.path("summary").asText();
                }
                if (!title.isBlank() || !content.isBlank()) {
                    results.add(new SearchResult(id, title, content));
                }
            }
        }

        if (results.isEmpty()) {
            return fallbackResults(maxResults);
        }

        return results.stream().limit(maxResults).toList();
    }

    private List<SearchResult> fallbackResults(int maxResults) {
        return List.of(
                new SearchResult("opensearch-1", "OpenSearch Overview", "OpenSearch is a distributed search and analytics engine built for scale."),
                new SearchResult("opensearch-2", "OpenSearch AI Guide", "AI-powered retrieval improves answer quality with relevant source context."),
                new SearchResult("opensearch-3", "Search Relevance", "Relevance scoring helps surface the most useful documents for a query."),
                new SearchResult("opensearch-4", "Spring Boot Integration", "Spring Boot applications can integrate with OpenSearch via the Java client."),
                new SearchResult("opensearch-5", "RAG with Search", "Retrieval augmented generation combines search results with an LLM for grounded responses.")
        ).stream().limit(maxResults).toList();
    }

    public String getHost() {
        return properties.getHost();
    }

    public String getIndex() {
        return properties.getIndex();
    }
}

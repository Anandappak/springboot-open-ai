package com.example.opensearchassistant.service;

import com.example.opensearchassistant.config.OpenAiProperties;
import com.example.opensearchassistant.config.OpenSearchProperties;
import com.example.opensearchassistant.model.SearchResult;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class AssistantServiceTest {

    @Test
    void buildContext_shouldIncludeTopResults() {
        OpenSearchProperties openSearchProperties = new OpenSearchProperties();
        OpenAiProperties openAiProperties = new OpenAiProperties();

        AssistantService service = new AssistantService(
                new OpenSearchService(openSearchProperties, WebClient.builder(), new ObjectMapper()),
                new OpenAiService(openAiProperties, WebClient.builder(), new ObjectMapper())
        );

        SearchResult first = new SearchResult("1", "Getting started with OpenSearch", "OpenSearch is a distributed search and analytics engine.");
        SearchResult second = new SearchResult("2", "AI retrieval patterns", "RAG combines semantic search with language models for grounded answers.");

        String context = service.buildContext(List.of(first, second));

        assertThat(context)
                .contains("Getting started with OpenSearch")
                .contains("OpenSearch is a distributed search and analytics engine.")
                .contains("AI retrieval patterns")
                .contains("RAG combines semantic search with language models for grounded answers.");
    }
}

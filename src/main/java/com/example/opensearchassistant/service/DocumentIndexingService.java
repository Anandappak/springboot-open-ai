package com.example.opensearchassistant.service;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import com.example.opensearchassistant.config.OpenSearchProperties;
import com.example.opensearchassistant.model.KnowledgeDocument;
import com.fasterxml.jackson.databind.ObjectMapper;

import reactor.core.publisher.Mono;

@Service
public class DocumentIndexingService {

    private static final Logger log = LoggerFactory.getLogger(DocumentIndexingService.class);

    private final OpenSearchProperties properties;
    private final WebClient webClient;
    private final ObjectMapper objectMapper;

    public DocumentIndexingService(OpenSearchProperties properties,
                                  WebClient.Builder webClientBuilder,
                                  ObjectMapper objectMapper) {
        this.properties = properties;
        this.webClient = webClientBuilder.build();
        this.objectMapper = objectMapper;
    }

    public void ensureIndexExists() {
        String endpoint = properties.getHost().replaceAll("/+$", "") + "/" + properties.getIndex();
        String mappingBody = """
                {
                  "settings": {
                    "index": {
                      "number_of_shards": 1,
                      "number_of_replicas": 0
                    }
                  },
                  "mappings": {
                    "properties": {
                      "id": { "type": "keyword" },
                      "title": { "type": "text", "analyzer": "standard" },
                      "summary": { "type": "text", "analyzer": "standard" },
                      "content": { "type": "text", "analyzer": "standard" }
                    }
                  }
                }
                """;

        try {
            webClient.put()
                    .uri(endpoint)
                    .headers(headers -> {
                        if (properties.getUsername() != null && !properties.getUsername().isBlank()
                                && properties.getPassword() != null && !properties.getPassword().isBlank()) {
                            headers.setBasicAuth(properties.getUsername(), properties.getPassword());
                        }
                    })
                    .bodyValue(mappingBody)
                    .retrieve()
                    .onStatus(HttpStatusCode::isError, clientResponse -> {
                        return clientResponse.bodyToMono(String.class)
                                .defaultIfEmpty("")
                                .flatMap(body -> {
                                    if (body.contains("resource_already_exists_exception")) {
                                        return Mono.empty();
                                    }
                                    return Mono.error(new IllegalStateException("OpenSearch ensureIndex failed: " + body));
                                });
                    })
                    .bodyToMono(String.class)
                    .block();

            log.info("Ensured OpenSearch index exists: {}", properties.getIndex());
        } catch (Exception ex) {
            log.warn("Unable to ensure OpenSearch index {} exists: {}", properties.getIndex(), ex.getMessage());
        }
    }

    public void indexDocuments(List<KnowledgeDocument> documents) {
        if (documents == null || documents.isEmpty()) {
            return;
        }

        ensureIndexExists();

        String endpoint = properties.getHost().replaceAll("/+$", "") + "/" + properties.getIndex();

        for (KnowledgeDocument document : documents) {
            try {
                String payload = objectMapper.writeValueAsString(
                        new KnowledgeDocument(document.id(), document.title(), document.summary(), document.content())
                );

                webClient.put()
                        .uri(endpoint + "/_doc/" + document.id())
                        .headers(headers -> {
                            if (properties.getUsername() != null && !properties.getUsername().isBlank()
                                    && properties.getPassword() != null && !properties.getPassword().isBlank()) {
                                headers.setBasicAuth(properties.getUsername(), properties.getPassword());
                            }
                        })
                        .bodyValue(payload)
                        .retrieve()
                        .onStatus(HttpStatusCode::isError, clientResponse -> {
                            return clientResponse.bodyToMono(String.class)
                                    .defaultIfEmpty("")
                                    .flatMap(body -> Mono.error(new IllegalStateException(
                                            "OpenSearch index failed: " + body)));
                        })
                        .bodyToMono(String.class)
                        .block();

                log.info("Indexed temple document {} -> {}", document.id(), document.title());
            } catch (Exception ex) {
                log.warn("OpenSearch indexing failed for {}: {}", document.id(), ex.getMessage());
            }
        }
    }
}

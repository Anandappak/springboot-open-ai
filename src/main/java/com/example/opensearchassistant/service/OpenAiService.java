package com.example.opensearchassistant.service;

import com.example.opensearchassistant.config.OpenAiProperties;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Service
public class OpenAiService {

    private static final Logger log = LoggerFactory.getLogger(OpenAiService.class);

    private final OpenAiProperties properties;
    private final WebClient webClient;
    private final ObjectMapper objectMapper;

    public OpenAiService(OpenAiProperties properties, WebClient.Builder webClientBuilder, ObjectMapper objectMapper) {
        this.properties = properties;
        this.webClient = webClientBuilder.build();
        this.objectMapper = objectMapper;
    }

    public String answer(String userQuery, String context) {
        if (properties.getApiKey() == null || properties.getApiKey().isBlank()) {
            return "OpenAI API key is not configured. Using retrieved context to provide a fallback answer.\n\n"
                    + "Based on the indexed material, relevant information for '" + userQuery + "' includes the following:\n"
                    + context;
        }

        try {
            String prompt = "Answer the user question using only the provided context.\n\nQuestion: " + userQuery + "\n\nContext:\n" + context;

            String responseBody = webClient.post()
                    .uri(properties.getBaseUrl().replaceAll("/+$", "") + "/chat/completions")
                    .header("Authorization", "Bearer " + properties.getApiKey())
                    .header("Content-Type", "application/json")
                    .bodyValue("""
                            {
                              "model": "%s",
                              "messages": [
                                {"role": "user", "content": "%s"}
                              ],
                              "temperature": 0.2
                            }
                            """.formatted(properties.getModel(), prompt.replace("\"", "\\\"")))
                    .retrieve()
                    .onStatus(HttpStatusCode::isError, clientResponse -> {
                        return clientResponse.bodyToMono(String.class)
                                .defaultIfEmpty("")
                                .flatMap(body -> Mono.error(new IllegalStateException("OpenAI request failed: " + body)));
                    })
                    .bodyToMono(String.class)
                    .block();

            JsonNode root = objectMapper.readTree(responseBody);
            JsonNode content = root.path("choices").path(0).path("message").path("content");
            if (content.isTextual()) {
                return content.asText();
            }
        } catch (Exception ex) {
            log.warn("OpenAI fallback triggered for query '{}': {}", userQuery, ex.getMessage());
        }

        return "This is a fallback answer generated from the retrieved search context for: " + userQuery + "\n\n" + context;
    }
}

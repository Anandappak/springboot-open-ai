package com.example.opensearchassistant.service;

import com.example.opensearchassistant.model.SearchResult;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AssistantService {

    private final OpenSearchService openSearchService;
    private final OpenAiService openAiService;

    public AssistantService(OpenSearchService openSearchService, OpenAiService openAiService) {
        this.openSearchService = openSearchService;
        this.openAiService = openAiService;
    }

    public String buildContext(List<SearchResult> results) {
        StringBuilder context = new StringBuilder();
        for (int i = 0; i < results.size(); i++) {
            SearchResult result = results.get(i);
            context.append("Result ").append(i + 1).append(":\n")
                    .append("Title: ").append(result.title()).append("\n")
                    .append("Content: ").append(result.content()).append("\n\n");
        }
        return context.toString();
    }

    public String buildPrompt(String query, List<SearchResult> results) {
        String contextText = buildContext(results);
        return "You are a helpful AI assistant for enterprise search. " +
                "Answer the user question using only the source material below. " +
                "If the answer is not present, say so clearly.\n\n" +
                "Question: " + query + "\n\n" +
                "Source material:\n" + contextText;
    }

    public String ask(String query, int maxResults) {
        List<SearchResult> results = openSearchService.search(query, maxResults);
        String prompt = buildPrompt(query, results);
        return openAiService.answer(query, prompt);
    }
}

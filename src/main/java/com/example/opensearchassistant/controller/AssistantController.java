package com.example.opensearchassistant.controller;

import com.example.opensearchassistant.model.AssistantRequest;
import com.example.opensearchassistant.model.AssistantResponse;
import com.example.opensearchassistant.model.ErrorResponse;
import com.example.opensearchassistant.model.SearchResult;
import com.example.opensearchassistant.service.AssistantService;
import com.example.opensearchassistant.service.OpenSearchService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
public class AssistantController {

    private final AssistantService assistantService;
    private final OpenSearchService openSearchService;

    public AssistantController(AssistantService assistantService, OpenSearchService openSearchService) {
        this.assistantService = assistantService;
        this.openSearchService = openSearchService;
    }

    @PostMapping("/ask")
    public ResponseEntity<?> ask(@Valid @RequestBody AssistantRequest request) {
        if (request == null || request.getQuery() == null || request.getQuery().isBlank()) {
            return ResponseEntity.badRequest().body(new ErrorResponse("Query must not be empty."));
        }

        try {
            List<SearchResult> results = openSearchService.search(request.getQuery(), request.getMaxResults());
            String answer = assistantService.ask(request.getQuery(), request.getMaxResults());
            return ResponseEntity.ok(new AssistantResponse(answer, results));
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.badRequest().body(new ErrorResponse(ex.getMessage()));
        } catch (Exception ex) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ErrorResponse("Unable to process the request."));
        }
    }

    @GetMapping("/health")
    public ResponseEntity<String> health() {
        return ResponseEntity.ok("OK");
    }
}

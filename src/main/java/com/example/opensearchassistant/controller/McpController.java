package com.example.opensearchassistant.controller;

import java.util.List;
import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.opensearchassistant.model.TempleMcpSearchRequest;
import com.example.opensearchassistant.model.TempleMcpSearchResponse;
import com.example.opensearchassistant.service.TempleManagementService;
import com.example.opensearchassistant.service.TempleMcpService;

@RestController
@RequestMapping("/api/mcp")
public class McpController {

    private final TempleMcpService templeMcpService;
    private final TempleManagementService templeManagementService;

    public McpController(TempleMcpService templeMcpService, TempleManagementService templeManagementService) {
        this.templeMcpService = templeMcpService;
        this.templeManagementService = templeManagementService;
    }

    @GetMapping("/health")
    public ResponseEntity<String> health() {
        return ResponseEntity.ok("Temple MCP server is running");
    }

    @GetMapping("/tools")
    public ResponseEntity<List<Map<String, Object>>> tools() {
        return ResponseEntity.ok(List.of(
                Map.of(
                        "name", "searchTempleData",
                        "description", "Search temple members, donations, and events using natural language",
                        "inputSchema", Map.of(
                                "type", "object",
                                "properties", Map.of(
                                        "query", Map.of("type", "string"),
                                        "entityType", Map.of("type", "string", "enum", List.of("all", "member", "donation", "event")),
                                        "filters", Map.of("type", "object")
                                ),
                                "required", List.of("query")
                        )
                ),
                Map.of(
                        "name", "getTempleDashboard",
                        "description", "Return temple dashboard metrics such as members, donations, and event counts",
                        "inputSchema", Map.of("type", "object", "properties", Map.of())
                )
        ));
    }

    @PostMapping("/search")
    public ResponseEntity<?> search(@RequestBody TempleMcpSearchRequest request) {
        if (request == null || request.query() == null || request.query().isBlank()) {
            return ResponseEntity.badRequest().body(Map.of("error", "Query must not be blank"));
        }

        return ResponseEntity.ok(new TempleMcpSearchResponse(
                templeMcpService.searchTempleData(request.query(), request.entityType(), request.filters())));
    }

    @GetMapping("/dashboard")
    public ResponseEntity<Map<String, Object>> dashboard() {
        var summary = templeManagementService.getDashboardSummary();
        return ResponseEntity.ok(Map.of(
                "summary", summary,
                "knowledgeDocuments", templeMcpService.buildTempleKnowledgeDocuments().size()
        ));
    }

    @PostMapping("/index")
    public ResponseEntity<Map<String, Object>> indexTempleKnowledge() {
        templeMcpService.syncTempleKnowledgeToSearch();
        return ResponseEntity.ok(Map.of(
                "status", "success",
                "indexedDocuments", templeMcpService.buildTempleKnowledgeDocuments().size()
        ));
    }
}

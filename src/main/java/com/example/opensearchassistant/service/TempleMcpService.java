package com.example.opensearchassistant.service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.opensearchassistant.model.KnowledgeDocument;
import com.example.opensearchassistant.model.SearchResult;
import com.example.opensearchassistant.model.TempleDonation;
import com.example.opensearchassistant.model.TempleEvent;
import com.example.opensearchassistant.model.TempleMember;
import com.example.opensearchassistant.repository.TempleDonationRepository;
import com.example.opensearchassistant.repository.TempleEventRepository;
import com.example.opensearchassistant.repository.TempleMemberRepository;

@Service
public class TempleMcpService {

    private final TempleMemberRepository templeMemberRepository;
    private final TempleDonationRepository templeDonationRepository;
    private final TempleEventRepository templeEventRepository;
    private final DocumentIndexingService documentIndexingService;

    @Autowired
    public TempleMcpService(TempleMemberRepository templeMemberRepository,
                           TempleDonationRepository templeDonationRepository,
                           TempleEventRepository templeEventRepository,
                           @Autowired(required = false) DocumentIndexingService documentIndexingService) {
        this.templeMemberRepository = templeMemberRepository;
        this.templeDonationRepository = templeDonationRepository;
        this.templeEventRepository = templeEventRepository;
        this.documentIndexingService = documentIndexingService;
    }

    public List<KnowledgeDocument> buildTempleKnowledgeDocuments() {
        List<KnowledgeDocument> documents = new ArrayList<>();

        for (TempleMember member : templeMemberRepository.findAll()) {
            String title = member.getName() + " - " + member.getRole();
            String content = "Temple member " + member.getName() + " holds the role of " + member.getRole()
                    + ". Phone: " + member.getPhoneNumber() + ". Address: " + member.getAddress() + ".";
            String summary = "Temple committee and service member information for " + member.getName();
            documents.add(new KnowledgeDocument("member:" + member.getId(), title, summary, content));
        }

        for (TempleDonation donation : templeDonationRepository.findAll()) {
            String title = donation.getDonorName() + " donated " + donation.getAmount() + " for " + donation.getPurpose();
            String content = "Donor " + donation.getDonorName() + " offered " + donation.getAmount()
                    + " INR for " + donation.getPurpose() + " on " + donation.getDonationDate() + "."
                    + " Payment method: " + donation.getPaymentMethod() + ".";
            String summary = "Temple donation record from " + donation.getDonorName();
            documents.add(new KnowledgeDocument("donation:" + donation.getId(), title, summary, content));
        }

        for (TempleEvent event : templeEventRepository.findAll()) {
            String title = event.getName() + " - " + event.getStatus();
            String content = "Temple event " + event.getName() + " is scheduled for " + event.getEventDate()
                    + ". Status: " + event.getStatus() + ". Description: " + event.getDescription() + ".";
            String summary = "Temple event and pooja schedule information for " + event.getName();
            documents.add(new KnowledgeDocument("event:" + event.getId(), title, summary, content));
        }

        return documents;
    }

    public void syncTempleKnowledgeToSearch() {
        if (documentIndexingService != null) {
            documentIndexingService.indexDocuments(buildTempleKnowledgeDocuments());
        }
    }

    public List<SearchResult> searchTempleData(String query, String entityType, Map<String, String> filters) {
        if (query == null || query.isBlank()) {
            return List.of();
        }

        syncTempleKnowledgeToSearch();

        String normalizedQuery = query.toLowerCase(Locale.ROOT);
        String normalizedEntityType = entityType == null ? "all" : entityType.toLowerCase(Locale.ROOT);
        List<SearchResult> results = new ArrayList<>();

        for (KnowledgeDocument document : buildTempleKnowledgeDocuments()) {
            if (!matchesEntityType(document.id(), normalizedEntityType)) {
                continue;
            }

            if (!matchesFilters(document, filters)) {
                continue;
            }

            String searchableText = (document.title() + " " + document.summary() + " " + document.content()).toLowerCase(Locale.ROOT);
            String[] terms = normalizedQuery.split("\\s+");
            boolean matchesPhrase = searchableText.contains(normalizedQuery);
            boolean matchesAnyTerm = Arrays.stream(terms)
                    .filter(term -> !term.isBlank())
                    .anyMatch(searchableText::contains);

            if (matchesPhrase || matchesAnyTerm) {
                results.add(new SearchResult(document.id(), document.title(), document.content()));
            }
        }

        if (results.isEmpty()) {
            results.add(new SearchResult("temple-mcp-empty",
                    "No temple record matched the search",
                    "The temple knowledge base did not return a direct match. Try a member name, event name, donation purpose, or role."));
        }

        return results.stream().limit(10).toList();
    }

    private boolean matchesEntityType(String documentId, String entityType) {
        if (entityType == null || entityType.isBlank() || "all".equals(entityType)) {
            return true;
        }
        return documentId.startsWith(entityType + ":");
    }

    private boolean matchesFilters(KnowledgeDocument document, Map<String, String> filters) {
        if (filters == null || filters.isEmpty()) {
            return true;
        }

        for (Map.Entry<String, String> entry : filters.entrySet()) {
            String key = entry.getKey() == null ? "" : entry.getKey().trim().toLowerCase(Locale.ROOT);
            String value = entry.getValue() == null ? "" : entry.getValue().trim().toLowerCase(Locale.ROOT);
            if (key.isBlank() || value.isBlank()) {
                continue;
            }

            String combined = (document.title() + " " + document.summary() + " " + document.content()).toLowerCase(Locale.ROOT);
            if (!combined.contains(value)) {
                return false;
            }

            if ("role".equals(key) && !combined.contains(value)) {
                return false;
            }
        }

        return true;
    }
}

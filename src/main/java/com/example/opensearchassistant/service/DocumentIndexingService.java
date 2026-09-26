package com.example.opensearchassistant.service;

import com.example.opensearchassistant.model.KnowledgeDocument;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class DocumentIndexingService {

    private static final Logger log = LoggerFactory.getLogger(DocumentIndexingService.class);

    public void indexDocuments(List<KnowledgeDocument> documents) {
        for (KnowledgeDocument document : documents) {
            log.info("Prepared document for indexing: {} - {}", document.id(), document.title());
        }
    }
}

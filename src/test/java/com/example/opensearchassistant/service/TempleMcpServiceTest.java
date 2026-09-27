package com.example.opensearchassistant.service;

import com.example.opensearchassistant.model.TempleDonation;
import com.example.opensearchassistant.model.TempleEvent;
import com.example.opensearchassistant.model.TempleMember;
import com.example.opensearchassistant.repository.TempleDonationRepository;
import com.example.opensearchassistant.repository.TempleEventRepository;
import com.example.opensearchassistant.repository.TempleMemberRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@Import(TempleMcpService.class)
class TempleMcpServiceTest {

    @Autowired
    private TempleMemberRepository templeMemberRepository;

    @Autowired
    private TempleDonationRepository templeDonationRepository;

    @Autowired
    private TempleEventRepository templeEventRepository;

    @Autowired
    private TempleMcpService templeMcpService;

    @Test
    void shouldBuildTempleKnowledgeDocumentsFromDatabase() {
        templeMemberRepository.save(new TempleMember(null, "Ravi", "Trustee", "0771234567", "North Lane"));
        templeDonationRepository.save(new TempleDonation(null, "Meera", new BigDecimal("2500.00"), "Festival", LocalDate.now()));
        templeEventRepository.save(new TempleEvent(null, "Navaratri", LocalDate.now().plusDays(3), "Festival celebration", "Planned"));

        List<com.example.opensearchassistant.model.KnowledgeDocument> documents = templeMcpService.buildTempleKnowledgeDocuments();

        assertThat(documents).isNotEmpty();
        assertThat(documents).anySatisfy(doc -> {
            assertThat(doc.title()).contains("Ravi");
            assertThat(doc.content()).contains("Trustee");
        });
        assertThat(documents).anySatisfy(doc -> {
            assertThat(doc.title()).contains("Festival");
            assertThat(doc.content()).contains("Meera");
        });
    }

    @Test
    void shouldSearchTempleDataByMemberAndEventKeywords() {
        templeMemberRepository.save(new TempleMember(null, "Kavya", "Volunteer", "0775555555", "Temple Road"));
        templeEventRepository.save(new TempleEvent(null, "Annual Pooja", LocalDate.now().plusDays(10), "Annual prayer service", "Scheduled"));

        List<com.example.opensearchassistant.model.SearchResult> results = templeMcpService.searchTempleData("pooja Kavya", "all", java.util.Map.of());

        assertThat(results).isNotEmpty();
        assertThat(results).anySatisfy(result -> {
            assertThat(result.title()).containsAnyOf("Kavya", "Annual Pooja");
        });
    }
}

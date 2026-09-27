package com.example.opensearchassistant.service;

import com.example.opensearchassistant.model.TempleDashboardSummary;
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
@Import(TempleManagementService.class)
class TempleManagementServiceTest {

    @Autowired
    private TempleMemberRepository templeMemberRepository;

    @Autowired
    private TempleDonationRepository templeDonationRepository;

    @Autowired
    private TempleEventRepository templeEventRepository;

    @Autowired
    private TempleManagementService service;

    @Test
    void dashboardSummary_shouldCountMembersDonationsAndEvents() {
        service.addMember(new TempleMember(null, "Ravi", "Trustee", "0771234567", "North Lane"));
        service.addMember(new TempleMember(null, "Meera", "Volunteer", "0777654321", "Temple Street"));

        service.addDonation(new TempleDonation(null, "Ravi", new BigDecimal("2500.00"), "Festival", LocalDate.now()));
        service.addDonation(new TempleDonation(null, "Meera", new BigDecimal("1000.00"), "General", LocalDate.now()));

        service.addEvent(new TempleEvent(null, "Pooja Festival", LocalDate.now().plusDays(3), "Temple festival celebration", "Planned"));

        TempleDashboardSummary summary = service.getDashboardSummary();

        assertThat(summary.totalMembers()).isEqualTo(2);
        assertThat(summary.totalDonations()).isEqualTo(2);
        assertThat(summary.totalEvents()).isEqualTo(1);
        assertThat(summary.totalDonationAmount()).isEqualByComparingTo(new BigDecimal("3500.00"));
    }

    @Test
    void paymentDetails_shouldBeStoredForOnlineDonation() {
        TempleDonation donation = new TempleDonation();
        donation.setDonorName("Sonia");
        donation.setAmount(new BigDecimal("2500.00"));
        donation.setPurpose("Festival support");
        donation.setDonationDate(LocalDate.now());
        donation.setPaymentMethod("UPI");
        donation.setUpiId("templedonation@upi");
        donation.setTransactionReference("UPI-1001");
        donation.setBankName("State Bank of India");
        donation.setAccountHolderName("Village Temple Trust");
        donation.setAccountNumber("123456789012");
        donation.setIfscCode("SBIN0001234");

        TempleDonation saved = service.addDonation(donation);

        assertThat(saved.getPaymentMethod()).isEqualTo("UPI");
        assertThat(saved.getUpiId()).isEqualTo("templedonation@upi");
        assertThat(saved.getTransactionReference()).isEqualTo("UPI-1001");
        assertThat(saved.getBankName()).isEqualTo("State Bank of India");
        assertThat(saved.getAccountNumber()).isEqualTo("123456789012");
    }

    @Test
    void members_shouldBeReturnedInInsertionOrder() {
        service.addMember(new TempleMember(null, "Asha", "Caretaker", "0770000001", "Village Road"));
        service.addMember(new TempleMember(null, "Kumar", "Priest", "0770000002", "Old Town"));

        List<TempleMember> members = service.getMembers();

        assertThat(members).hasSize(2);
        assertThat(members.get(0).getName()).isEqualTo("Asha");
        assertThat(members.get(1).getName()).isEqualTo("Kumar");
    }
}

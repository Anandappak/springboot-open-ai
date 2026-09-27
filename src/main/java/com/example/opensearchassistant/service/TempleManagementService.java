package com.example.opensearchassistant.service;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.stereotype.Service;

import com.example.opensearchassistant.model.TempleDashboardSummary;
import com.example.opensearchassistant.model.TempleDonation;
import com.example.opensearchassistant.model.TempleEvent;
import com.example.opensearchassistant.model.TempleMember;
import com.example.opensearchassistant.repository.TempleDonationRepository;
import com.example.opensearchassistant.repository.TempleEventRepository;
import com.example.opensearchassistant.repository.TempleMemberRepository;

@Service
public class TempleManagementService {
    private final TempleMemberRepository templeMemberRepository;
    private final TempleDonationRepository templeDonationRepository;
    private final TempleEventRepository templeEventRepository;

    public TempleManagementService(TempleMemberRepository templeMemberRepository,
                                  TempleDonationRepository templeDonationRepository,
                                  TempleEventRepository templeEventRepository) {
        this.templeMemberRepository = templeMemberRepository;
        this.templeDonationRepository = templeDonationRepository;
        this.templeEventRepository = templeEventRepository;
    }

    public TempleMember addMember(TempleMember member) {
        if (member == null) {
            throw new IllegalArgumentException("Member must not be null");
        }
        if (member.getName() == null || member.getName().isBlank()) {
            throw new IllegalArgumentException("Member name is required");
        }

        return templeMemberRepository.save(member);
    }

    public List<TempleMember> getMembers() {
        return templeMemberRepository.findAll();
    }

    public TempleMember updateMember(Long id, TempleMember member) {
        TempleMember existing = templeMemberRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Member not found"));
        existing.setName(member.getName());
        existing.setRole(member.getRole());
        existing.setPhoneNumber(member.getPhoneNumber());
        existing.setAddress(member.getAddress());
        return templeMemberRepository.save(existing);
    }

    public void deleteMember(Long id) {
        templeMemberRepository.deleteById(id);
    }

    public TempleDonation addDonation(TempleDonation donation) {
        if (donation == null) {
            throw new IllegalArgumentException("Donation must not be null");
        }
        if (donation.getDonorName() == null || donation.getDonorName().isBlank()) {
            throw new IllegalArgumentException("Donor name is required");
        }
        if (donation.getAmount() == null) {
            throw new IllegalArgumentException("Donation amount is required");
        }

        normalizePaymentDetails(donation);
        return templeDonationRepository.save(donation);
    }

    public List<TempleDonation> getDonations() {
        return templeDonationRepository.findAll();
    }

    public TempleDonation updateDonation(Long id, TempleDonation donation) {
        TempleDonation existing = templeDonationRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Donation not found"));
        existing.setDonorName(donation.getDonorName());
        existing.setAmount(donation.getAmount());
        existing.setPurpose(donation.getPurpose());
        existing.setDonationDate(donation.getDonationDate());
        existing.setPaymentMethod(donation.getPaymentMethod());
        existing.setTransactionReference(donation.getTransactionReference());
        existing.setUpiId(donation.getUpiId());
        existing.setQrCodeLabel(donation.getQrCodeLabel());
        existing.setBankName(donation.getBankName());
        existing.setAccountHolderName(donation.getAccountHolderName());
        existing.setAccountNumber(donation.getAccountNumber());
        existing.setIfscCode(donation.getIfscCode());
        existing.setPaymentStatus(donation.getPaymentStatus());
        normalizePaymentDetails(existing);
        return templeDonationRepository.save(existing);
    }

    private void normalizePaymentDetails(TempleDonation donation) {
        if (donation.getPaymentMethod() == null || donation.getPaymentMethod().isBlank()) {
            donation.setPaymentMethod("UPI");
        }
        if (donation.getUpiId() == null || donation.getUpiId().isBlank()) {
            donation.setUpiId("templedonation@upi");
        }
        if (donation.getQrCodeLabel() == null || donation.getQrCodeLabel().isBlank()) {
            donation.setQrCodeLabel("Temple Donation QR");
        }
        if (donation.getBankName() == null || donation.getBankName().isBlank()) {
            donation.setBankName("State Bank of India");
        }
        if (donation.getAccountHolderName() == null || donation.getAccountHolderName().isBlank()) {
            donation.setAccountHolderName("Village Temple Trust");
        }
        if (donation.getAccountNumber() == null || donation.getAccountNumber().isBlank()) {
            donation.setAccountNumber("123456789012");
        }
        if (donation.getIfscCode() == null || donation.getIfscCode().isBlank()) {
            donation.setIfscCode("SBIN0001234");
        }
        if (donation.getPaymentStatus() == null || donation.getPaymentStatus().isBlank()) {
            donation.setPaymentStatus("PAID");
        }
    }

    public void deleteDonation(Long id) {
        templeDonationRepository.deleteById(id);
    }

    public TempleEvent addEvent(TempleEvent event) {
        if (event == null) {
            throw new IllegalArgumentException("Event must not be null");
        }
        if (event.getName() == null || event.getName().isBlank()) {
            throw new IllegalArgumentException("Event name is required");
        }

        return templeEventRepository.save(event);
    }

    public List<TempleEvent> getEvents() {
        return templeEventRepository.findAll();
    }

    public TempleEvent updateEvent(Long id, TempleEvent event) {
        TempleEvent existing = templeEventRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Event not found"));
        existing.setName(event.getName());
        existing.setEventDate(event.getEventDate());
        existing.setDescription(event.getDescription());
        existing.setStatus(event.getStatus());
        return templeEventRepository.save(existing);
    }

    public void deleteEvent(Long id) {
        templeEventRepository.deleteById(id);
    }

    public TempleDashboardSummary getDashboardSummary() {
        List<TempleDonation> donations = templeDonationRepository.findAll();
        BigDecimal totalDonationAmount = donations.stream()
                .map(TempleDonation::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        return new TempleDashboardSummary(
                templeMemberRepository.count(),
                donations.size(),
                templeEventRepository.count(),
                totalDonationAmount
        );
    }
}

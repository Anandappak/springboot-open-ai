package com.example.opensearchassistant.service;

import com.example.opensearchassistant.model.TempleDashboardSummary;
import com.example.opensearchassistant.model.TempleDonation;
import com.example.opensearchassistant.model.TempleEvent;
import com.example.opensearchassistant.model.TempleMember;
import com.example.opensearchassistant.repository.TempleDonationRepository;
import com.example.opensearchassistant.repository.TempleEventRepository;
import com.example.opensearchassistant.repository.TempleMemberRepository;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

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
        return templeDonationRepository.save(existing);
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

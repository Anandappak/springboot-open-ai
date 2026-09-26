package com.example.opensearchassistant.controller;

import com.example.opensearchassistant.model.TempleDashboardSummary;
import com.example.opensearchassistant.model.TempleDonation;
import com.example.opensearchassistant.model.TempleEvent;
import com.example.opensearchassistant.model.TempleMember;
import com.example.opensearchassistant.service.TempleManagementService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/temple")
public class TempleManagementController {

    private final TempleManagementService templeManagementService;

    public TempleManagementController(TempleManagementService templeManagementService) {
        this.templeManagementService = templeManagementService;
    }

    @GetMapping("/health")
    public ResponseEntity<String> health() {
        return ResponseEntity.ok("Temple management service is running");
    }

    @GetMapping("/dashboard")
    public ResponseEntity<TempleDashboardSummary> dashboard() {
        return ResponseEntity.ok(templeManagementService.getDashboardSummary());
    }

    @GetMapping("/members")
    public ResponseEntity<List<TempleMember>> getMembers() {
        return ResponseEntity.ok(templeManagementService.getMembers());
    }

    @PostMapping("/members")
    public ResponseEntity<TempleMember> addMember(@RequestBody TempleMember member) {
        return ResponseEntity.status(HttpStatus.CREATED).body(templeManagementService.addMember(member));
    }

    @PostMapping("/members/{id}")
    public ResponseEntity<TempleMember> updateMember(@PathVariable Long id, @RequestBody TempleMember member) {
        return ResponseEntity.ok(templeManagementService.updateMember(id, member));
    }

    @DeleteMapping("/members/{id}")
    public ResponseEntity<Void> deleteMember(@PathVariable Long id) {
        templeManagementService.deleteMember(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/donations")
    public ResponseEntity<List<TempleDonation>> getDonations() {
        return ResponseEntity.ok(templeManagementService.getDonations());
    }

    @PostMapping("/donations")
    public ResponseEntity<TempleDonation> addDonation(@RequestBody TempleDonation donation) {
        return ResponseEntity.status(HttpStatus.CREATED).body(templeManagementService.addDonation(donation));
    }

    @PostMapping("/donations/{id}")
    public ResponseEntity<TempleDonation> updateDonation(@PathVariable Long id, @RequestBody TempleDonation donation) {
        return ResponseEntity.ok(templeManagementService.updateDonation(id, donation));
    }

    @DeleteMapping("/donations/{id}")
    public ResponseEntity<Void> deleteDonation(@PathVariable Long id) {
        templeManagementService.deleteDonation(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/events")
    public ResponseEntity<List<TempleEvent>> getEvents() {
        return ResponseEntity.ok(templeManagementService.getEvents());
    }

    @PostMapping("/events")
    public ResponseEntity<TempleEvent> addEvent(@RequestBody TempleEvent event) {
        return ResponseEntity.status(HttpStatus.CREATED).body(templeManagementService.addEvent(event));
    }

    @PostMapping("/events/{id}")
    public ResponseEntity<TempleEvent> updateEvent(@PathVariable Long id, @RequestBody TempleEvent event) {
        return ResponseEntity.ok(templeManagementService.updateEvent(id, event));
    }

    @DeleteMapping("/events/{id}")
    public ResponseEntity<Void> deleteEvent(@PathVariable Long id) {
        templeManagementService.deleteEvent(id);
        return ResponseEntity.noContent().build();
    }
}

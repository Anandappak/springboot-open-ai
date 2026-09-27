package com.example.opensearchassistant.controller;

import com.example.opensearchassistant.model.TempleDonation;
import com.example.opensearchassistant.service.TempleManagementService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/public")
public class PublicDonationController {

    private final TempleManagementService templeManagementService;

    public PublicDonationController(TempleManagementService templeManagementService) {
        this.templeManagementService = templeManagementService;
    }

    @PostMapping("/donations")
    public ResponseEntity<TempleDonation> submitDonation(@RequestBody TempleDonation donation) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(templeManagementService.addDonation(donation));
    }
}

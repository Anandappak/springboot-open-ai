package com.example.opensearchassistant.repository;

import com.example.opensearchassistant.model.TempleDonation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TempleDonationRepository extends JpaRepository<TempleDonation, Long> {
}

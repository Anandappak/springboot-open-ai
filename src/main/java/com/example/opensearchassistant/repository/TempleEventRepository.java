package com.example.opensearchassistant.repository;

import com.example.opensearchassistant.model.TempleEvent;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TempleEventRepository extends JpaRepository<TempleEvent, Long> {
}

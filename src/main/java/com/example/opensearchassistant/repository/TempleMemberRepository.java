package com.example.opensearchassistant.repository;

import com.example.opensearchassistant.model.TempleMember;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TempleMemberRepository extends JpaRepository<TempleMember, Long> {
}

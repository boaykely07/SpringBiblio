package com.example.spring_practice.repository;

import com.example.spring_practice.model.entities.StatutProlongementEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface StatutProlongementRepository extends JpaRepository<StatutProlongementEntity, Long> {
    Optional<StatutProlongementEntity> findByCodeStatut(String codeStatut);
} 
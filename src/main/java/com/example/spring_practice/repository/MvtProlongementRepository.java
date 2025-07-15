package com.example.spring_practice.repository;

import com.example.spring_practice.model.entities.MvtProlongementEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MvtProlongementRepository extends JpaRepository<MvtProlongementEntity, Long> {
    // Méthodes personnalisées si besoin
} 
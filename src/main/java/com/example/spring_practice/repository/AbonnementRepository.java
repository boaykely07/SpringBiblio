package com.example.spring_practice.repository;

import com.example.spring_practice.model.entities.AbonnementEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AbonnementRepository extends JpaRepository<AbonnementEntity, Long> {
} 
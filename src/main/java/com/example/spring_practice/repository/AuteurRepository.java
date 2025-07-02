package com.example.spring_practice.repository;

import com.example.spring_practice.model.entities.AuteurEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AuteurRepository extends JpaRepository<AuteurEntity, Long> {
} 
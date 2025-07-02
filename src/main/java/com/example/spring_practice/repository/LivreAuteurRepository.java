package com.example.spring_practice.repository;

import com.example.spring_practice.model.entities.LivreAuteurEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LivreAuteurRepository extends JpaRepository<LivreAuteurEntity, Long> {
} 
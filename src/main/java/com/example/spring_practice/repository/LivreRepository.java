package com.example.spring_practice.repository;

import com.example.spring_practice.model.entities.LivreEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface LivreRepository extends JpaRepository<LivreEntity, Long> {
    List<LivreEntity> findByTitreContainingIgnoreCase(String titre);
} 
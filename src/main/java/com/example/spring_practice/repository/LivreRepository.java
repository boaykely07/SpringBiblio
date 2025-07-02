package com.example.spring_practice.repository;

import com.example.spring_practice.model.entities.LivreEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LivreRepository extends JpaRepository<LivreEntity, Long> {
} 
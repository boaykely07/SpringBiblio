package com.example.spring_practice.repository;

import com.example.spring_practice.model.entities.StatutEmpruntEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface StatutEmpruntRepository extends JpaRepository<StatutEmpruntEntity, Long> {
    // Méthodes personnalisées si besoin
    Optional<StatutEmpruntEntity> findByCodeStatut(String codeStatut);
} 
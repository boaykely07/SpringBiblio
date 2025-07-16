package com.example.spring_practice.repository;

import com.example.spring_practice.model.entities.ProlongementEntity;
import org.springframework.data.jpa.repository.JpaRepository;
 
public interface ProlongementRepository extends JpaRepository<ProlongementEntity, Long> {
    // Méthodes personnalisées si besoin
    ProlongementEntity findTopByEmpruntIdOrderByDateFinDesc(Long empruntId);
    java.util.List<ProlongementEntity> findByEmprunt_Adherent_Id(Long adherentId);
} 
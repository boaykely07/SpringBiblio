package com.example.spring_practice.repository;

import com.example.spring_practice.model.entities.PenaliteEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface PenaliteRepository extends JpaRepository<PenaliteEntity, Long> {
    List<PenaliteEntity> findByAdherentId(Long adherentId);
    // Récupère la pénalité avec la date de fin la plus tardive pour un adhérent
    PenaliteEntity findTopByAdherentIdOrderByDateDebutDesc(Long adherentId);
} 
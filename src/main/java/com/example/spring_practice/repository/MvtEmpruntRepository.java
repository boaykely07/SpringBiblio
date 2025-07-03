package com.example.spring_practice.repository;

import com.example.spring_practice.model.entities.MvtEmpruntEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface MvtEmpruntRepository extends JpaRepository<MvtEmpruntEntity, Long> {
    // Méthodes personnalisées si besoin
    Optional<MvtEmpruntEntity> findTopByEmpruntIdOrderByDateMouvementDesc(Long empruntId);
} 
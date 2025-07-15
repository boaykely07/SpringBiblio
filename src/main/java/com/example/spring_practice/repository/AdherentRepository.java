package com.example.spring_practice.repository;

import com.example.spring_practice.model.entities.AdherentEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AdherentRepository extends JpaRepository<AdherentEntity, Long> {
    AdherentEntity findByUtilisateurId(Long utilisateurId);
}
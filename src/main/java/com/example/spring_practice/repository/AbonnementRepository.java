package com.example.spring_practice.repository;

import com.example.spring_practice.model.entities.AbonnementEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface AbonnementRepository extends JpaRepository<AbonnementEntity, Long> {
    List<AbonnementEntity> findByAdherentId(Long adherentId);
} 
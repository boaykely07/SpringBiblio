package com.example.spring_practice.repository;

import com.example.spring_practice.model.entities.PenaliteEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface PenaliteRepository extends JpaRepository<PenaliteEntity, Long> {
    List<PenaliteEntity> findByAdherentId(Long adherentId);
} 
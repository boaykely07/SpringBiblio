package com.example.spring_practice.repository;

import com.example.spring_practice.model.entities.PenaliteEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PenaliteRepository extends JpaRepository<PenaliteEntity, Long> {
} 
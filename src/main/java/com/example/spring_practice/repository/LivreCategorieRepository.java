package com.example.spring_practice.repository;

import com.example.spring_practice.model.entities.LivreCategorieEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LivreCategorieRepository extends JpaRepository<LivreCategorieEntity, Long> {
} 
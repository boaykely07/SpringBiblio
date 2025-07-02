package com.example.spring_practice.repository;

import com.example.spring_practice.model.entities.CategorieEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CategorieRepository extends JpaRepository<CategorieEntity, Long> {
} 
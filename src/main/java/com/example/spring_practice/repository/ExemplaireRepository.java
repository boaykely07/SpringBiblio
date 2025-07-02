package com.example.spring_practice.repository;

import com.example.spring_practice.model.entities.ExemplaireEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ExemplaireRepository extends JpaRepository<ExemplaireEntity, Long> {
} 
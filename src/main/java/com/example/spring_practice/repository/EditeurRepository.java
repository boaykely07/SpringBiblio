package com.example.spring_practice.repository;

import com.example.spring_practice.model.entities.EditeurEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EditeurRepository extends JpaRepository<EditeurEntity, Long> {
} 
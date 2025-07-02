package com.example.spring_practice.repository;

import com.example.spring_practice.model.entities.TypeEmpruntEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TypeEmpruntRepository extends JpaRepository<TypeEmpruntEntity, Long> {
} 
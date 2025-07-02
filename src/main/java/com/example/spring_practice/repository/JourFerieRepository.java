package com.example.spring_practice.repository;

import com.example.spring_practice.model.entities.JourFerieEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface JourFerieRepository extends JpaRepository<JourFerieEntity, java.time.LocalDate> {
} 
package com.example.spring_practice.repository;

import com.example.spring_practice.model.entities.StatutReservationEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface StatutReservationRepository extends JpaRepository<StatutReservationEntity, Long> {
} 
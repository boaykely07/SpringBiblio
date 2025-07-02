package com.example.spring_practice.repository;

import com.example.spring_practice.model.entities.MvtReservationEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MvtReservationRepository extends JpaRepository<MvtReservationEntity, Long> {
} 
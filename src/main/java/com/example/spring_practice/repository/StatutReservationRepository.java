package com.example.spring_practice.repository;

import com.example.spring_practice.model.entities.StatutReservationEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface StatutReservationRepository extends JpaRepository<StatutReservationEntity, Long> {
    Optional<StatutReservationEntity> findByCodeStatut(String codeStatut);
} 
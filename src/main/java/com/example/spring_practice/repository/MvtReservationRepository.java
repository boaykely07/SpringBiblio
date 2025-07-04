package com.example.spring_practice.repository;

import com.example.spring_practice.model.entities.MvtReservationEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface MvtReservationRepository extends JpaRepository<MvtReservationEntity, Long> {
    Optional<MvtReservationEntity> findTopByReservationIdOrderByDateMouvementDesc(Long reservationId);
} 
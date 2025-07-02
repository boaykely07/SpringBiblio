package com.example.spring_practice.repository;

import com.example.spring_practice.model.entities.ProfilAdherentEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProfilAdherentRepository extends JpaRepository<ProfilAdherentEntity, Long> {
}
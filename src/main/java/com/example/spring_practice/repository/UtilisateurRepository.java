package com.example.spring_practice.repository;

import com.example.spring_practice.model.entities.UtilisateurEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface UtilisateurRepository extends JpaRepository<UtilisateurEntity, Long> {
    Optional<UtilisateurEntity> findByEmail(String email);
}
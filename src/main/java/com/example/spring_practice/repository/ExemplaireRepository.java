package com.example.spring_practice.repository;

import com.example.spring_practice.model.entities.ExemplaireEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ExemplaireRepository extends JpaRepository<ExemplaireEntity, Long> {
    List<ExemplaireEntity> findByLivreId(Long livreId);
} 
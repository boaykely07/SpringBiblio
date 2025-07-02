package com.example.spring_practice.repository;

import com.example.spring_practice.model.entities.EmpruntEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface EmpruntRepository extends JpaRepository<EmpruntEntity, Long> {
    // You can add custom query methods here if needed
} 
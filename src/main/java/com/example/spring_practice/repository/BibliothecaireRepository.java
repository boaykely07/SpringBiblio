package com.example.spring_practice.repository;

import com.example.spring_practice.model.entities.BibliothecaireEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BibliothecaireRepository extends JpaRepository<BibliothecaireEntity, Long> {
}
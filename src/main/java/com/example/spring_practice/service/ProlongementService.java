package com.example.spring_practice.service;

import com.example.spring_practice.model.entities.ProlongementEntity;
import com.example.spring_practice.repository.ProlongementRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ProlongementService {
    @Autowired
    private ProlongementRepository prolongementRepository;

    public List<ProlongementEntity> findAll() {
        return prolongementRepository.findAll();
    }

    public Optional<ProlongementEntity> findById(Long id) {
        return prolongementRepository.findById(id);
    }

    public ProlongementEntity save(ProlongementEntity prolongement) {
        return prolongementRepository.save(prolongement);
    }

    public void deleteById(Long id) {
        prolongementRepository.deleteById(id);
    }
} 
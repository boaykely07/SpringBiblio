package com.example.spring_practice.service;

import com.example.spring_practice.model.entities.PenaliteEntity;
import com.example.spring_practice.repository.PenaliteRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class PenaliteService {
    @Autowired
    private PenaliteRepository penaliteRepository;

    public List<PenaliteEntity> findAll() {
        return penaliteRepository.findAll();
    }

    public Optional<PenaliteEntity> findById(Long id) {
        return penaliteRepository.findById(id);
    }

    public PenaliteEntity save(PenaliteEntity penalite) {
        return penaliteRepository.save(penalite);
    }

    public void deleteById(Long id) {
        penaliteRepository.deleteById(id);
    }
}

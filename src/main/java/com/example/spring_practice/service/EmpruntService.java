package com.example.spring_practice.service;

import com.example.spring_practice.model.entities.EmpruntEntity;
import com.example.spring_practice.repository.EmpruntRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class EmpruntService {
    @Autowired
    private EmpruntRepository empruntRepository;

    public List<EmpruntEntity> findAll() {
        return empruntRepository.findAll();
    }

    public Optional<EmpruntEntity> findById(Long id) {
        return empruntRepository.findById(id);
    }

    public EmpruntEntity save(EmpruntEntity emprunt) {
        return empruntRepository.save(emprunt);
    }

    public void deleteById(Long id) {
        empruntRepository.deleteById(id);
    }
} 
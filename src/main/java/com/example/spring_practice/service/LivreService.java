package com.example.spring_practice.service;

import com.example.spring_practice.model.entities.LivreEntity;
import com.example.spring_practice.repository.LivreRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class LivreService {
    @Autowired
    private LivreRepository livreRepository;

    public List<LivreEntity> findAll() {
        return livreRepository.findAll();
    }

    public List<LivreEntity> findByTitreContainingIgnoreCase(String titre) {
        return livreRepository.findByTitreContainingIgnoreCase(titre);
    }

    public LivreEntity findById(Long id) {
        return livreRepository.findById(id).orElse(null);
    }

    public LivreEntity save(LivreEntity livre) {
        return livreRepository.save(livre);
    }
} 
package com.example.spring_practice.service;

import com.example.spring_practice.model.entities.ProfilAdherentEntity;
import com.example.spring_practice.repository.ProfilAdherentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ProfilAdherentService {
    @Autowired
    private ProfilAdherentRepository profilAdherentRepository;

    public List<ProfilAdherentEntity> findAll() {
        return profilAdherentRepository.findAll();
    }

    public ProfilAdherentEntity save(ProfilAdherentEntity profilAdherent) {
        return profilAdherentRepository.save(profilAdherent);
    }

    public ProfilAdherentEntity findById(Long id) {
        return profilAdherentRepository.findById(id).orElse(null);
    }
} 
package com.example.spring_practice.service;

import com.example.spring_practice.model.entities.AbonnementEntity;
import com.example.spring_practice.repository.AbonnementRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class AbonnementService {
    @Autowired
    private AbonnementRepository abonnementRepository;

    public List<AbonnementEntity> findAll() {
        return abonnementRepository.findAll();
    }

    public Optional<AbonnementEntity> findById(Long id) {
        return abonnementRepository.findById(id);
    }

    public AbonnementEntity save(AbonnementEntity abonnement) {
        return abonnementRepository.save(abonnement);
    }

    public void deleteById(Long id) {
        abonnementRepository.deleteById(id);
    }
}

package com.example.spring_practice.service;

import com.example.spring_practice.model.entities.ProlongementEntity;
import com.example.spring_practice.repository.ProlongementRepository;
import com.example.spring_practice.repository.AbonnementRepository;
import com.example.spring_practice.model.entities.AbonnementEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ProlongementService {
    @Autowired
    private ProlongementRepository prolongementRepository;

    @Autowired
    private AbonnementRepository abonnementRepository;

    public List<ProlongementEntity> findAll() {
        return prolongementRepository.findAll();
    }

    public Optional<ProlongementEntity> findById(Long id) {
        return prolongementRepository.findById(id);
    }

    public ProlongementEntity save(ProlongementEntity prolongement) {
        // Vérification de l'abonnement actif à la nouvelle date de fin
        if (prolongement.getEmprunt() != null && prolongement.getEmprunt().getAdherent() != null && prolongement.getDateFin() != null) {
            Long adherentId = prolongement.getEmprunt().getAdherent().getId();
            java.time.LocalDate dateFin = prolongement.getDateFin().toLocalDate();
            java.util.List<AbonnementEntity> abonnements = abonnementRepository.findByAdherentId(adherentId);
            boolean actif = abonnements.stream().anyMatch(ab ->
                (dateFin.isEqual(ab.getDateDebut()) || dateFin.isAfter(ab.getDateDebut())) &&
                (dateFin.isEqual(ab.getDateFin()) || dateFin.isBefore(ab.getDateFin()))
            );
            if (!actif) {
                throw new IllegalStateException("L'adhérent n'a pas d'abonnement actif à la nouvelle date de fin du prolongement.");
            }
        }
        return prolongementRepository.save(prolongement);
    }

    public void deleteById(Long id) {
        prolongementRepository.deleteById(id);
    }
} 
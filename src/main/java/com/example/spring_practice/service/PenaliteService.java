package com.example.spring_practice.service;

import com.example.spring_practice.model.entities.PenaliteEntity;
import com.example.spring_practice.repository.PenaliteRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
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
        if (penalite.getAdherent() != null) {
            // Trouver la dernière pénalité pour cet adhérent
            PenaliteEntity dernierePenalite = penaliteRepository.findTopByAdherentIdOrderByDateDebutDesc(penalite.getAdherent().getId());
            if (dernierePenalite != null) {
                // Calculer la date de fin de la dernière pénalité (inclusif)
                LocalDate finDerniere = dernierePenalite.getDateDebut().plusDays(dernierePenalite.getJour() - 1);
                // Forcer la date de début de la nouvelle pénalité à être après la fin de la dernière
                LocalDate dateDebutProposee = penalite.getDateDebut() != null ? penalite.getDateDebut() : LocalDate.now();
                if (!dateDebutProposee.isAfter(finDerniere)) {
                    penalite.setDateDebut(finDerniere.plusDays(1));
                }
            } else {
                // Si aucune pénalité précédente, utiliser la date proposée ou la date actuelle
                if (penalite.getDateDebut() == null) {
                    penalite.setDateDebut(LocalDate.now());
                }
            }
        }
        return penaliteRepository.save(penalite);
    }

    public void deleteById(Long id) {
        penaliteRepository.deleteById(id);
    }

    // Renvoie la dernière pénalité (par date de début) pour un adhérent
    public PenaliteEntity findLastPenaliteForAdherent(Long adherentId) {
        return penaliteRepository.findTopByAdherentIdOrderByDateDebutDesc(adherentId);
    }

    public List<PenaliteEntity> findByAdherentId(Long adherentId) {
        return penaliteRepository.findByAdherentId(adherentId);
    }
}

package com.example.spring_practice.service;

import com.example.spring_practice.model.entities.ReservationEntity;
import com.example.spring_practice.model.entities.MvtReservationEntity;
import com.example.spring_practice.model.entities.StatutReservationEntity;
import com.example.spring_practice.model.entities.PenaliteEntity;
import com.example.spring_practice.repository.ReservationRepository;
import com.example.spring_practice.repository.MvtReservationRepository;
import com.example.spring_practice.repository.StatutReservationRepository;
import com.example.spring_practice.repository.PenaliteRepository;
import com.example.spring_practice.repository.ExemplaireRepository;
import com.example.spring_practice.model.entities.ExemplaireEntity;
import com.example.spring_practice.service.EmpruntService;
import com.example.spring_practice.model.entities.EmpruntEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class ReservationService {
    @Autowired
    private ReservationRepository reservationRepository;
    
    @Autowired
    private MvtReservationRepository mvtReservationRepository;
    
    @Autowired
    private StatutReservationRepository statutReservationRepository;

    @Autowired
    private PenaliteRepository penaliteRepository;

    @Autowired
    private ExemplaireRepository exemplaireRepository;

    @Autowired
    private EmpruntService empruntService;

    public List<ReservationEntity> findAll() {
        return reservationRepository.findAll();
    }

    public Optional<ReservationEntity> findById(Long id) {
        return reservationRepository.findById(id);
    }

    public ReservationEntity save(ReservationEntity reservation) {
        // Vérification des pénalités pour la date de réservation
        Long adherentId = reservation.getAdherent().getId();
        List<PenaliteEntity> penalites = penaliteRepository.findByAdherentId(adherentId);
        for (PenaliteEntity p : penalites) {
            java.time.LocalDate debut = p.getDateDebut();
            java.time.LocalDate fin = debut.plusDays(p.getJour() - 1);
            java.time.LocalDate dateResa = reservation.getDateAReserver();
            if ((dateResa.isEqual(debut) || dateResa.isAfter(debut)) && dateResa.isBefore(fin.plusDays(1))) {
                throw new IllegalStateException("L'adhérent a une pénalité couvrant la date de réservation et ne peut pas réserver.");
            }
        }
        // Vérification de la disponibilité d'exemplaires pour la date de réservation
        Long livreId = reservation.getLivre().getId();
        List<ExemplaireEntity> exemplaires = exemplaireRepository.findByLivreId(livreId);
        int total = exemplaires.stream().mapToInt(ExemplaireEntity::getQuantite).sum();
        int disponible = total;
        for (ExemplaireEntity ex : exemplaires) {
            List<EmpruntEntity> empruntsEx = empruntService.findAll().stream()
                .filter(e -> e.getExemplaire().getId().equals(ex.getId()))
                .toList();
            for (EmpruntEntity e : empruntsEx) {
                String statut = empruntService.getLastStatutForEmprunt(e.getId());
                java.time.LocalDateTime debutExist = e.getDateEmprunt();
                java.time.LocalDateTime finExist = e.getDateRetourPrevue();
                java.time.LocalDateTime dateResaDebut = reservation.getDateAReserver().atStartOfDay();
                java.time.LocalDateTime dateResaFin = reservation.getDateAReserver().atTime(23,59,59);
                boolean chevauche = !dateResaFin.isBefore(debutExist) && !dateResaDebut.isAfter(finExist);
                if (("En cours".equalsIgnoreCase(statut) || "Retard".equalsIgnoreCase(statut)) && chevauche) {
                    disponible -= 1;
                } else if ("Rendu".equalsIgnoreCase(statut) && chevauche) {
                    disponible += 1;
                }
            }
        }
        if (disponible <= 0) {
            throw new IllegalStateException("Aucun exemplaire disponible pour ce livre à la date demandée.");
        }
        // Sauvegarde de la réservation
        ReservationEntity savedReservation = reservationRepository.save(reservation);
        
        // Création du mouvement de réservation avec statut "En attente"
        StatutReservationEntity statutEnAttente = statutReservationRepository.findByCodeStatut("En attente")
            .orElseThrow(() -> new IllegalStateException("Statut 'En attente' introuvable"));
        
        MvtReservationEntity mvt = new MvtReservationEntity();
        mvt.setReservation(savedReservation);
        mvt.setStatutNouveau(statutEnAttente);
        mvt.setDateMouvement(LocalDateTime.now());
        mvtReservationRepository.save(mvt);
        
        return savedReservation;
    }

    public void deleteById(Long id) {
        reservationRepository.deleteById(id);
    }
} 
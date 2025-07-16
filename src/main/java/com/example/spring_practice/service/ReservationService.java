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
import com.example.spring_practice.repository.AbonnementRepository;
import com.example.spring_practice.model.entities.AbonnementEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.time.*;

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

    @Autowired
    private AbonnementRepository abonnementRepository;

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
            LocalDate debut = p.getDateDebut();
            LocalDate fin = debut.plusDays(p.getJour() - 1);
            LocalDate dateResa = reservation.getDateAReserver();
            if ((dateResa.isEqual(debut) || dateResa.isAfter(debut)) && dateResa.isBefore(fin.plusDays(1))) {
                throw new IllegalStateException("L'adhérent a une pénalité couvrant la date de réservation et ne peut pas réserver.");
            }
        }
        // Vérification de l'abonnement actif à la date de réservation
        List<AbonnementEntity> abonnements = abonnementRepository.findByAdherentId(adherentId);
        LocalDate dateResa = reservation.getDateAReserver();
        boolean actif = abonnements.stream().anyMatch(ab ->
            (dateResa.isEqual(ab.getDateDebut()) || dateResa.isAfter(ab.getDateDebut())) &&
            (dateResa.isEqual(ab.getDateFin()) || dateResa.isBefore(ab.getDateFin()))
        );
        if (!actif) {
            throw new IllegalStateException("L'adhérent n'a pas d'abonnement actif à la date de la réservation.");
        }
        // Vérification de la disponibilité d'exemplaires pour la date de réservation
        Long livreId = reservation.getLivre().getId();
        List<ExemplaireEntity> exemplaires = exemplaireRepository.findByLivreId(livreId);
        int disponible = 0;
        for (ExemplaireEntity ex : exemplaires) {
            List<EmpruntEntity> empruntsEx = empruntService.findAll().stream()
                .filter(e -> e.getExemplaire().getId().equals(ex.getId()))
                .toList();
            boolean occupe = false;
            for (EmpruntEntity e : empruntsEx) {
                String statut = empruntService.getLastStatutForEmprunt(e.getId());
                LocalDateTime debutExist = e.getDateEmprunt();
                LocalDateTime finExist = e.getDateRetourPrevue();
                LocalDateTime dateResaDebut = reservation.getDateAReserver().atStartOfDay();
                LocalDateTime dateResaFin = reservation.getDateAReserver().atTime(23,59,59);
                boolean chevauche = !dateResaFin.isBefore(debutExist) && !dateResaDebut.isAfter(finExist);
                if ("En cours".equalsIgnoreCase(statut) && chevauche) {
                    occupe = true;
                    break;
                }
            }
            if (!occupe) {
                disponible++;
            }
        }
        if (disponible <= 0) {
            throw new IllegalStateException("Aucun exemplaire disponible pour ce livre à la date demandée.");
        }
        // Vérification du quota de réservations en attente pour le profil adhérent
        int quotaReservation = reservation.getAdherent().getProfil().getReservationPret();
        List<ReservationEntity> reservationsAdh = reservationRepository.findByAdherentId(adherentId);
        long nbEnAttente = reservationsAdh.stream().filter(r -> {
            // On regarde le dernier statut de chaque réservation
            return mvtReservationRepository.findTopByReservationIdOrderByDateMouvementDesc(r.getId())
                .map(mvt -> "En attente".equalsIgnoreCase(mvt.getStatutNouveau().getCodeStatut()))
                .orElse(false);
        }).count();
        if (nbEnAttente >= quotaReservation) {
            throw new IllegalStateException("Quota de réservations en attente atteint pour ce profil adhérent.");
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
package com.example.spring_practice.controller;

import com.example.spring_practice.model.entities.ReservationEntity;
import com.example.spring_practice.model.entities.LivreEntity;
import com.example.spring_practice.model.entities.AdherentEntity;
import com.example.spring_practice.model.entities.MvtReservationEntity;
import com.example.spring_practice.model.entities.StatutReservationEntity;
import com.example.spring_practice.service.ReservationService;
import com.example.spring_practice.repository.LivreRepository;
import com.example.spring_practice.repository.AdherentRepository;
import com.example.spring_practice.repository.MvtReservationRepository;
import com.example.spring_practice.repository.StatutReservationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.HashMap;

@Controller
@RequestMapping("/reservations")
public class ReservationController {
    @Autowired
    private ReservationService reservationService;
    
    @Autowired
    private LivreRepository livreRepository;
    
    @Autowired
    private AdherentRepository adherentRepository;

    @Autowired
    private MvtReservationRepository mvtReservationRepository;
    @Autowired
    private StatutReservationRepository statutReservationRepository;

    @GetMapping
    public String listReservations(Model model) {
        List<ReservationEntity> reservations = reservationService.findAll();
        // Map pour les statuts courants
        Map<Long, String> statuts = new HashMap<>();
        for (ReservationEntity r : reservations) {
            MvtReservationEntity mvt = mvtReservationRepository.findTopByReservationIdOrderByDateMouvementDesc(r.getId()).orElse(null);
            statuts.put(r.getId(), mvt != null ? mvt.getStatutNouveau().getCodeStatut() : "Inconnu");
        }
        model.addAttribute("reservations", reservations);
        model.addAttribute("statuts", statuts);
        return "pages/admin/reservation_list";
    }

    @GetMapping("/new")
    public String newReservationForm(@RequestParam Long livreId, Model model) {
        LivreEntity livre = livreRepository.findById(livreId)
            .orElseThrow(() -> new IllegalArgumentException("Livre introuvable"));
        model.addAttribute("livre", livre);
        return "pages/client/reserver_client";
    }

    @PostMapping("/new")
    public String createReservation(@RequestParam Long livreId, 
                                   @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dateAReserver,
                                   Model model) {
        try {
            LivreEntity livre = livreRepository.findById(livreId)
                .orElseThrow(() -> new IllegalArgumentException("Livre introuvable"));
            // TODO: Récupérer l'adhérent connecté (pour l'instant, on prend le premier)
            AdherentEntity adherent = adherentRepository.findAll().get(0);
            ReservationEntity reservation = new ReservationEntity();
            reservation.setLivre(livre);
            reservation.setAdherent(adherent);
            reservation.setDateDemande(LocalDateTime.now());
            reservation.setDateAReserver(dateAReserver);
            reservationService.save(reservation);
            model.addAttribute("livre", livre);
            model.addAttribute("success", "Réservation créée avec succès");
            return "pages/client/reserver_client";
        } catch (Exception e) {
            model.addAttribute("error", "Erreur lors de la création de la réservation: " + e.getMessage());
            LivreEntity livre = livreRepository.findById(livreId)
                .orElseThrow(() -> new IllegalArgumentException("Livre introuvable"));
            model.addAttribute("livre", livre);
            return "pages/client/reserver_client";
        }
    }

    @PostMapping("/valider/{id}")
    public String validerReservation(@PathVariable Long id) {
        ReservationEntity reservation = reservationService.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("Réservation non trouvée"));
        StatutReservationEntity statutValidee = statutReservationRepository.findByCodeStatut("Validée")
            .orElseThrow(() -> new IllegalStateException("Statut 'Validée' introuvable"));
        MvtReservationEntity mvt = new MvtReservationEntity();
        mvt.setReservation(reservation);
        mvt.setStatutNouveau(statutValidee);
        mvt.setDateMouvement(LocalDateTime.now());
        mvtReservationRepository.save(mvt);
        return "redirect:/reservations";
    }

    @GetMapping("/delete/{id}")
    public String deleteReservation(@PathVariable Long id) {
        reservationService.deleteById(id);
        return "redirect:/reservations";
    }
} 
package com.example.spring_practice.controller;

import com.example.spring_practice.model.entities.PenaliteEntity;
import com.example.spring_practice.model.entities.AdherentEntity;
import com.example.spring_practice.model.entities.EmpruntEntity;
import com.example.spring_practice.service.PenaliteService;
import com.example.spring_practice.repository.AdherentRepository;
import com.example.spring_practice.repository.EmpruntRepository;
import com.example.spring_practice.service.EmpruntService;
import com.example.spring_practice.model.entities.StatutEmpruntEntity;
import com.example.spring_practice.model.entities.MvtEmpruntEntity;
import com.example.spring_practice.repository.StatutEmpruntRepository;
import com.example.spring_practice.repository.MvtEmpruntRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.time.LocalDateTime;

@Controller
@RequestMapping("/penalites")
public class PenaliteController {
    @Autowired
    private PenaliteService penaliteService;
    @Autowired
    private AdherentRepository adherentRepository;
    @Autowired
    private EmpruntRepository empruntRepository;
    @Autowired
    private EmpruntService empruntService;
    @Autowired
    private StatutEmpruntRepository statutEmpruntRepository;
    @Autowired
    private MvtEmpruntRepository mvtEmpruntRepository;

    @GetMapping
    public String listPenalites(Model model) {
        List<PenaliteEntity> penalites = penaliteService.findAll();
        model.addAttribute("penalites", penalites);
        return "pages/admin/penalite_list";
    }

    @GetMapping("/new")
    public String newPenaliteForm(Model model) {
        model.addAttribute("penalite", new PenaliteEntity());
        model.addAttribute("adherents", adherentRepository.findAll());
        model.addAttribute("emprunts", empruntRepository.findAll());
        return "pages/admin/penalite_form";
    }

    @PostMapping
    public String savePenalite(@ModelAttribute PenaliteEntity penalite, Model model) {
        // Correction pour le mode auto : recharger l'emprunt et l'adhérent si besoin
        if (penalite.getEmprunt() == null || penalite.getEmprunt().getId() == null) {
            model.addAttribute("error", "Emprunt manquant pour la pénalité.");
            return "pages/admin/penalite_form";
        }
        EmpruntEntity emprunt = empruntRepository.findById(penalite.getEmprunt().getId()).orElse(null);
        if (emprunt == null) {
            model.addAttribute("error", "Emprunt introuvable.");
            return "pages/admin/penalite_form";
        }
        penalite.setEmprunt(emprunt);
        penalite.setAdherent(emprunt.getAdherent());
        // Vérifier le statut de l'emprunt
        String statut = empruntService.getLastStatutForEmprunt(emprunt.getId());
        penaliteService.save(penalite);
        return "redirect:/penalites";
    }

    @GetMapping("/delete/{id}")
    public String deletePenalite(@PathVariable Long id) {
        penaliteService.deleteById(id);
        return "redirect:/penalites";
    }

    @GetMapping("/auto")
    public String autoPenaliteForm(@RequestParam Long empruntId, @RequestParam java.time.LocalDate dateDebut, Model model) {
        EmpruntEntity emprunt = empruntRepository.findById(empruntId).orElse(null);
        if (emprunt == null) {
            return "redirect:/emprunts";
        }
        PenaliteEntity penalite = new PenaliteEntity();
        penalite.setEmprunt(emprunt);
        penalite.setAdherent(emprunt.getAdherent());
        penalite.setDateDebut(dateDebut);
        model.addAttribute("penalite", penalite);
        model.addAttribute("autoMode", true);
        return "pages/admin/penalite_form";
    }

    // Affichage des pénalités pour le client connecté
    @GetMapping("/client")
    public String listPenalitesClient(Model model) {
        // TODO: Récupérer l'adhérent connecté dynamiquement
        AdherentEntity adherent = adherentRepository.findAll().get(0); // à remplacer par l'authentification réelle
        List<PenaliteEntity> penalites = penaliteService.findByAdherentId(adherent.getId());
        int totalJours = penalites.stream().mapToInt(PenaliteEntity::getJour).sum();
        model.addAttribute("penalites", penalites);
        model.addAttribute("totalJours", totalJours);
        return "pages/client/penalite_list_client";
    }
}

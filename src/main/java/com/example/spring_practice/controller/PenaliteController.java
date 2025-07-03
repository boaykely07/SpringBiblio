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
        // Vérifier le statut de l'emprunt
        String statut = empruntService.getLastStatutForEmprunt(penalite.getEmprunt().getId());
        if ("Rendu".equalsIgnoreCase(statut)) {
            model.addAttribute("penalite", penalite);
            model.addAttribute("adherents", adherentRepository.findAll());
            model.addAttribute("emprunts", empruntRepository.findAll());
            model.addAttribute("error", "Impossible d'ajouter une pénalité sur un emprunt déjà rendu.");
            return "pages/admin/penalite_form";
        }
        penaliteService.save(penalite);
        // Changer le statut de l'emprunt en 'Retard'
        StatutEmpruntEntity statutRetard = statutEmpruntRepository.findByCodeStatut("Retard")
            .orElseThrow(() -> new IllegalStateException("Statut 'Retard' introuvable"));
        MvtEmpruntEntity mvt = new MvtEmpruntEntity();
        mvt.setEmprunt(penalite.getEmprunt());
        mvt.setStatutNouveau(statutRetard);
        mvt.setDateMouvement(LocalDateTime.now());
        mvtEmpruntRepository.save(mvt);
        return "redirect:/penalites";
    }

    @GetMapping("/delete/{id}")
    public String deletePenalite(@PathVariable Long id) {
        penaliteService.deleteById(id);
        return "redirect:/penalites";
    }
}

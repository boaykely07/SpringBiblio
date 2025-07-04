package com.example.spring_practice.controller;

import com.example.spring_practice.model.entities.ProlongementEntity;
import com.example.spring_practice.model.entities.EmpruntEntity;
import com.example.spring_practice.service.ProlongementService;
import com.example.spring_practice.repository.EmpruntRepository;
import com.example.spring_practice.service.EmpruntService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.List;
import java.util.Optional;
import java.time.LocalDateTime;

@Controller
@RequestMapping("/prolongements")
public class ProlongementController {
    @Autowired
    private ProlongementService prolongementService;
    @Autowired
    private EmpruntRepository empruntRepository;
    @Autowired
    private EmpruntService empruntService;

    @GetMapping
    public String listProlongements(Model model) {
        List<ProlongementEntity> prolongements = prolongementService.findAll();
        model.addAttribute("prolongements", prolongements);
        return "pages/admin/prolongement_list";
    }

    @GetMapping("/new")
    public String newProlongementForm(Model model) {
        model.addAttribute("prolongement", new ProlongementEntity());
        List<EmpruntEntity> emprunts = empruntRepository.findAll();
        List<EmpruntEntity> empruntsEnCours = emprunts.stream()
            .filter(e -> "En cours".equalsIgnoreCase(empruntService.getLastStatutForEmprunt(e.getId())))
            .toList();
        model.addAttribute("emprunts", empruntsEnCours);
        return "pages/admin/prolongement_form";
    }

    @PostMapping
    public String saveProlongement(@ModelAttribute ProlongementEntity prolongement, Model model) {
        // Recharge l'emprunt depuis la base pour avoir tous ses champs
        if (prolongement.getEmprunt() != null && prolongement.getEmprunt().getId() != null) {
            prolongement.setEmprunt(
                empruntRepository.findById(prolongement.getEmprunt().getId())
                    .orElse(null)
            );
        }
        LocalDateTime ancienneDateFin = null;
        if (prolongement.getEmprunt() != null) {
            ancienneDateFin = prolongement.getEmprunt().getDateRetourPrevue();
        }
        if (prolongement.getDateFin() == null || ancienneDateFin == null) {
            model.addAttribute("prolongement", prolongement);
            List<EmpruntEntity> emprunts = empruntRepository.findAll();
            List<EmpruntEntity> empruntsEnCours = emprunts.stream()
                .filter(e -> "En cours".equalsIgnoreCase(empruntService.getLastStatutForEmprunt(e.getId())))
                .toList();
            model.addAttribute("emprunts", empruntsEnCours);
            model.addAttribute("error", "Veuillez sélectionner un emprunt valide et saisir une nouvelle date de fin.");
            return "pages/admin/prolongement_form";
        }
        if (!prolongement.getDateFin().isAfter(ancienneDateFin)) {
            model.addAttribute("prolongement", prolongement);
            List<EmpruntEntity> emprunts = empruntRepository.findAll();
            List<EmpruntEntity> empruntsEnCours = emprunts.stream()
                .filter(e -> "En cours".equalsIgnoreCase(empruntService.getLastStatutForEmprunt(e.getId())))
                .toList();
            model.addAttribute("emprunts", empruntsEnCours);
            model.addAttribute("error", "La nouvelle date de fin doit être postérieure à la date de fin actuelle de l'emprunt.");
            return "pages/admin/prolongement_form";
        }
        prolongement.setDateProlongement(LocalDateTime.now());
        prolongementService.save(prolongement);
        // Met à jour la date de retour prévue de l'emprunt
        EmpruntEntity emprunt = prolongement.getEmprunt();
        emprunt.setDateRetourPrevue(prolongement.getDateFin());
        empruntRepository.save(emprunt);
        return "redirect:/prolongements";
    }

    @GetMapping("/delete/{id}")
    public String deleteProlongement(@PathVariable Long id) {
        prolongementService.deleteById(id);
        return "redirect:/prolongements";
    }
}

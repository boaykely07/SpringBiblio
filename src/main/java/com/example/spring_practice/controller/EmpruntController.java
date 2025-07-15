package com.example.spring_practice.controller;

import com.example.spring_practice.model.entities.EmpruntEntity;
import com.example.spring_practice.service.EmpruntService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import com.example.spring_practice.model.entities.ExemplaireEntity;
import com.example.spring_practice.model.entities.AdherentEntity;
import com.example.spring_practice.model.entities.TypeEmpruntEntity;
import com.example.spring_practice.repository.ExemplaireRepository;
import com.example.spring_practice.repository.AdherentRepository;
import com.example.spring_practice.repository.TypeEmpruntRepository;
import org.springframework.format.annotation.DateTimeFormat;
import com.example.spring_practice.repository.ProlongementRepository;
import com.example.spring_practice.model.entities.ProlongementEntity;
import com.example.spring_practice.service.PenaliteService;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import com.example.spring_practice.repository.MvtProlongementRepository;

import java.util.List;
import java.util.Optional;
import java.util.Map;
import java.util.HashMap;

@Controller
@RequestMapping("/emprunts")
public class EmpruntController {
    @Autowired
    private EmpruntService empruntService;
    @Autowired
    private ExemplaireRepository exemplaireRepository;
    @Autowired
    private AdherentRepository adherentRepository;
    @Autowired
    private TypeEmpruntRepository typeEmpruntRepository;
    @Autowired
    private ProlongementRepository prolongementRepository;
    @Autowired
    private PenaliteService penaliteService;
    @Autowired
    private MvtProlongementRepository mvtProlongementRepository;

    @GetMapping
    public String listEmprunts(Model model) {
        List<EmpruntEntity> emprunts = empruntService.findAll();
        Map<Long, String> statuts = new HashMap<>();
        Map<Long, java.time.LocalDateTime> dernieresProlongations = new HashMap<>();
        for (EmpruntEntity e : emprunts) {
            statuts.put(e.getId(), empruntService.getLastStatutForEmprunt(e.getId()));
            ProlongementEntity prolongement = prolongementRepository.findTopByEmpruntIdOrderByDateFinDesc(e.getId());
            java.time.LocalDateTime dateFinAAfficher = e.getDateRetourPrevue();
            if (prolongement != null && prolongement.getDateFin() != null) {
                com.example.spring_practice.model.entities.MvtProlongementEntity dernierMvt =
                    mvtProlongementRepository.findAll().stream()
                        .filter(m -> m.getProlongement().getId().equals(prolongement.getId()))
                        .max(java.util.Comparator.comparing(com.example.spring_practice.model.entities.MvtProlongementEntity::getDateMouvement))
                        .orElse(null);
                if (dernierMvt != null && "Valide".equalsIgnoreCase(dernierMvt.getStatutNouveau().getCodeStatut())) {
                    dateFinAAfficher = prolongement.getDateFin();
                }
            }
            dernieresProlongations.put(e.getId(), dateFinAAfficher);
        }
        model.addAttribute("emprunts", emprunts);
        model.addAttribute("statuts", statuts);
        model.addAttribute("dernieresProlongations", dernieresProlongations);
        return "pages/admin/list_emprunt";
    }

    @GetMapping("/{id}")
    public String getEmprunt(@PathVariable Long id, Model model) {
        Optional<EmpruntEntity> emprunt = empruntService.findById(id);
        emprunt.ifPresent(e -> model.addAttribute("emprunt", e));
        return "emprunts/detail";
    }

    @GetMapping("/new")
    public String newEmpruntForm(Model model) {
        model.addAttribute("emprunt", new EmpruntEntity());
        model.addAttribute("exemplaires", exemplaireRepository.findAll());
        model.addAttribute("adherents", adherentRepository.findAll());
        model.addAttribute("typesEmprunt", typeEmpruntRepository.findAll());
        return "pages/admin/emprunts_form";
    }

    @PostMapping
    public String saveEmprunt(@ModelAttribute EmpruntEntity emprunt, Model model) {
        try {
            // Recharge l'exemplaire complet depuis la base
            if (emprunt.getExemplaire() != null && emprunt.getExemplaire().getId() != null) {
                ExemplaireEntity ex = exemplaireRepository.findById(emprunt.getExemplaire().getId())
                    .orElse(null);
                emprunt.setExemplaire(ex);
            }
            empruntService.save(emprunt);
            return "redirect:/emprunts";
        } catch (IllegalStateException e) {
            model.addAttribute("emprunt", emprunt);
            model.addAttribute("exemplaires", exemplaireRepository.findAll());
            model.addAttribute("adherents", adherentRepository.findAll());
            model.addAttribute("typesEmprunt", typeEmpruntRepository.findAll());
            model.addAttribute("error", e.getMessage());
            return "pages/admin/emprunts_form";
        }
    }

    @GetMapping("/delete/{id}")
    public String deleteEmprunt(@PathVariable Long id) {
        empruntService.deleteById(id);
        return "redirect:/emprunts";
    }

    @GetMapping("/return/{id}")
    public String showReturnForm(@PathVariable Long id, Model model) {
        EmpruntEntity emprunt = empruntService.findById(id).orElse(null);
        if (emprunt == null) {
            return "redirect:/emprunts";
        }
        model.addAttribute("emprunt", emprunt);
        return "pages/admin/emprunt_return_form";
    }

    @PostMapping("/return/{id}")
    public String processReturn(@PathVariable Long id,
                                @RequestParam("dateRetour") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) java.time.LocalDateTime dateRetour,
                                Model model,
                                RedirectAttributes redirectAttributes) {
        EmpruntEntity emprunt = empruntService.findById(id).orElse(null);
        if (emprunt == null) {
            redirectAttributes.addFlashAttribute("error", "Emprunt introuvable.");
            return "redirect:/emprunts";
        }
        empruntService.returnEmprunt(id, dateRetour);
        java.time.LocalDateTime datePrevue = emprunt.getDateRetourPrevue();
        ProlongementEntity prolongement = prolongementRepository.findTopByEmpruntIdOrderByDateFinDesc(id);
        if (prolongement != null && prolongement.getDateFin() != null) {
            com.example.spring_practice.model.entities.MvtProlongementEntity dernierMvt =
                mvtProlongementRepository.findAll().stream()
                    .filter(m -> m.getProlongement().getId().equals(prolongement.getId()))
                    .max(java.util.Comparator.comparing(com.example.spring_practice.model.entities.MvtProlongementEntity::getDateMouvement))
                    .orElse(null);
            if (dernierMvt != null && "Valide".equalsIgnoreCase(dernierMvt.getStatutNouveau().getCodeStatut())) {
                datePrevue = prolongement.getDateFin();
            }
        }
        if (dateRetour.isAfter(datePrevue)) {
            com.example.spring_practice.model.entities.PenaliteEntity penalite = new com.example.spring_practice.model.entities.PenaliteEntity();
            penalite.setEmprunt(emprunt);
            penalite.setAdherent(emprunt.getAdherent());
            java.time.LocalDate dateDebutPenalite = dateRetour.toLocalDate();
            com.example.spring_practice.model.entities.PenaliteEntity dernierePenalite = penaliteService
                .findLastPenaliteForAdherent(emprunt.getAdherent().getId());
            if (dernierePenalite != null) {
                java.time.LocalDate finDerniere = dernierePenalite.getDateDebut().plusDays(dernierePenalite.getJour() - 1);
                if (!dateDebutPenalite.isAfter(finDerniere)) {
                    dateDebutPenalite = finDerniere.plusDays(1);
                }
            }
            penalite.setDateDebut(dateDebutPenalite);
            int quotaPenalitesJours = emprunt.getAdherent().getProfil().getQuotaPenalitesJours();
            penalite.setJour(quotaPenalitesJours);
            penalite.setRaison("en retard");
            penaliteService.save(penalite);
            redirectAttributes.addFlashAttribute("error", "Livre rendu avec pénalité pour retard (" + quotaPenalitesJours + " jours).");
            return "redirect:/emprunts";
        }
        redirectAttributes.addFlashAttribute("success", "Livre rendu avec succès.");
        return "redirect:/emprunts";
    }

    // Affichage de la liste des emprunts côté client
    @GetMapping("/mes-emprunts")
    public String mesEmprunts(Model model, @RequestParam(value = "success", required = false) String success) {
        com.example.spring_practice.model.entities.AdherentEntity adherent = adherentRepository.findAll().get(0);
        java.util.List<com.example.spring_practice.model.entities.EmpruntEntity> emprunts = empruntService.findByAdherentId(adherent.getId());
        java.util.Map<Long, String> statuts = new java.util.HashMap<>();
        java.util.Map<Long, java.time.LocalDateTime> dernieresProlongations = new java.util.HashMap<>();
        for (com.example.spring_practice.model.entities.EmpruntEntity e : emprunts) {
            statuts.put(e.getId(), empruntService.getLastStatutForEmprunt(e.getId()));
            com.example.spring_practice.model.entities.ProlongementEntity prolongement = prolongementRepository.findTopByEmpruntIdOrderByDateFinDesc(e.getId());
            if (prolongement != null) {
                dernieresProlongations.put(e.getId(), prolongement.getDateFin());
            }
        }
        model.addAttribute("emprunts", emprunts);
        model.addAttribute("statuts", statuts);
        model.addAttribute("dernieresProlongations", dernieresProlongations);
        model.addAttribute("activePage_client", "mes-emprunts");
        if (success != null) {
            model.addAttribute("success", success);
        }
        return "pages/client/mes_emprunts";
    }
} 
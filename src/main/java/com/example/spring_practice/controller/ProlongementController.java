package com.example.spring_practice.controller;

import com.example.spring_practice.model.entities.ProlongementEntity;
import com.example.spring_practice.model.entities.EmpruntEntity;
import com.example.spring_practice.service.ProlongementService;
import com.example.spring_practice.repository.EmpruntRepository;
import com.example.spring_practice.service.EmpruntService;
import com.example.spring_practice.repository.StatutProlongementRepository;
import com.example.spring_practice.repository.MvtProlongementRepository;
import com.example.spring_practice.repository.AdherentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

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
    @Autowired
    private StatutProlongementRepository statutProlongementRepository;
    @Autowired
    private MvtProlongementRepository mvtProlongementRepository;
    @Autowired
    private AdherentRepository adherentRepository;

    @GetMapping
    public String listProlongements(Model model) {
        List<ProlongementEntity> prolongements = prolongementService.findAll();
        java.util.Map<Long, String> statutsProlongement = new java.util.HashMap<>();
        for (ProlongementEntity p : prolongements) {
            com.example.spring_practice.model.entities.MvtProlongementEntity mvt = mvtProlongementRepository.findAll().stream()
                .filter(m -> m.getProlongement().getId().equals(p.getId()))
                .max(java.util.Comparator.comparing(com.example.spring_practice.model.entities.MvtProlongementEntity::getDateMouvement))
                .orElse(null);
            statutsProlongement.put(p.getId(), mvt != null ? mvt.getStatutNouveau().getCodeStatut() : "Inconnu");
        }
        model.addAttribute("prolongements", prolongements);
        model.addAttribute("statutsProlongement", statutsProlongement);
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

    // Affichage du formulaire de demande de prolongement côté client
    @GetMapping("/client/new")
    public String newProlongementClientForm(@RequestParam(required = false) Long livreId, @RequestParam(required = false) Long empruntId, Model model) {
        // TODO: remplacer par l'adhérent connecté
        com.example.spring_practice.model.entities.AdherentEntity adherent = adherentRepository.findAll().get(0);
        java.util.List<com.example.spring_practice.model.entities.EmpruntEntity> emprunts;
        if (empruntId != null) {
            emprunts = java.util.List.of(empruntRepository.findById(empruntId).orElse(null));
        } else if (livreId != null) {
            emprunts = empruntRepository.findByAdherentId(adherent.getId()).stream()
                .filter(e -> e.getExemplaire().getLivre().getId().equals(livreId) && e.getDateRetourReelle() == null)
                .toList();
        } else {
            emprunts = empruntRepository.findByAdherentId(adherent.getId());
        }
        model.addAttribute("prolongement", new com.example.spring_practice.model.entities.ProlongementEntity());
        model.addAttribute("emprunts", emprunts);
        model.addAttribute("clientMode", true);
        return "pages/client/prolongement_form_client";
    }

    // Traitement de la demande de prolongement côté client
    @PostMapping("/client/new")
    public String saveProlongementClient(@ModelAttribute com.example.spring_practice.model.entities.ProlongementEntity prolongement, RedirectAttributes redirectAttributes, Model model) {
        if (prolongement.getEmprunt() != null && prolongement.getEmprunt().getId() != null) {
            prolongement.setEmprunt(
                empruntRepository.findById(prolongement.getEmprunt().getId())
                    .orElse(null)
            );
        }
        prolongement.setDateProlongement(java.time.LocalDateTime.now());
        prolongementService.save(prolongement);
        com.example.spring_practice.model.entities.StatutProlongementEntity statutEnAttente = statutProlongementRepository.findByCodeStatut("En attente")
            .orElseThrow(() -> new IllegalStateException("Statut 'En attente' introuvable"));
        com.example.spring_practice.model.entities.MvtProlongementEntity mvt = new com.example.spring_practice.model.entities.MvtProlongementEntity();
        mvt.setProlongement(prolongement);
        mvt.setStatutNouveau(statutEnAttente);
        mvt.setDateMouvement(java.time.LocalDateTime.now());
        mvtProlongementRepository.save(mvt);
        redirectAttributes.addAttribute("success", "Demande de prolongement envoyée.");
        return "redirect:/emprunts/mes-emprunts";
    }

    // Action pour valider un prolongement
    @PostMapping("/valider/{id}")
    public String validerProlongement(@PathVariable Long id) {
        ProlongementEntity prolongement = prolongementService.findById(id).orElse(null);
        if (prolongement == null) return "redirect:/prolongements";
        com.example.spring_practice.model.entities.StatutProlongementEntity statutValide = statutProlongementRepository.findByCodeStatut("Valide")
            .orElseThrow(() -> new IllegalStateException("Statut 'Valide' introuvable"));
        com.example.spring_practice.model.entities.MvtProlongementEntity mvt = new com.example.spring_practice.model.entities.MvtProlongementEntity();
        mvt.setProlongement(prolongement);
        mvt.setStatutNouveau(statutValide);
        mvt.setDateMouvement(java.time.LocalDateTime.now());
        mvtProlongementRepository.save(mvt);
        return "redirect:/prolongements";
    }
}

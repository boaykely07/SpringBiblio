package com.example.spring_practice.controller;

import com.example.spring_practice.service.EmpruntService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import com.example.spring_practice.model.entities.*;
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
import com.example.spring_practice.repository.LivreRepository;
import com.example.spring_practice.model.entities.UtilisateurEntity;
import com.example.spring_practice.model.entities.MvtProlongementEntity;
import jakarta.servlet.http.HttpSession;


import java.util.*;

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
    @Autowired
    private com.example.spring_practice.repository.LivreRepository livreRepository;

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
                MvtProlongementEntity dernierMvt =
                    mvtProlongementRepository.findAll().stream()
                        .filter(m -> m.getProlongement().getId().equals(prolongement.getId()))
                        .max(Comparator.comparing(MvtProlongementEntity::getDateMouvement))
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
        model.addAttribute("livres", livreRepository.findAll());
        model.addAttribute("adherents", adherentRepository.findAll());
        model.addAttribute("typesEmprunt", typeEmpruntRepository.findAll());
        return "pages/admin/emprunts_form";
    }

    @PostMapping
    public String saveEmprunt(@ModelAttribute EmpruntEntity emprunt, @RequestParam("livre.id") Long livreId, Model model) {
        try {
            if (livreId == null) {
                model.addAttribute("error", "Aucun livre sélectionné.");
                model.addAttribute("livres", livreRepository.findAll());
                model.addAttribute("adherents", adherentRepository.findAll());
                model.addAttribute("typesEmprunt", typeEmpruntRepository.findAll());
                return "pages/admin/emprunts_form";
            }
            List<ExemplaireEntity> exemplaires = exemplaireRepository.findAll();
            java.time.LocalDateTime dateDebut = emprunt.getDateEmprunt();
            java.time.LocalDateTime dateFin = emprunt.getDateRetourPrevue();
            ExemplaireEntity exemplaireDispo = exemplaires.stream()
                .filter(ex -> ex.getLivre().getId().equals(livreId))
                .filter(ex -> empruntService.isExemplaireDisponiblePourPeriode(ex.getId(), dateDebut, dateFin))
                .findFirst().orElse(null);
            if (exemplaireDispo == null) {
                model.addAttribute("error", "Aucun exemplaire disponible pour ce livre à la date demandée.");
                model.addAttribute("emprunt", emprunt); // <-- Ajouté pour éviter l'erreur Thymeleaf
                model.addAttribute("livres", livreRepository.findAll());
                model.addAttribute("adherents", adherentRepository.findAll());
                model.addAttribute("typesEmprunt", typeEmpruntRepository.findAll());
                return "pages/admin/emprunts_form";
            }
            emprunt.setExemplaire(exemplaireDispo);
            empruntService.save(emprunt);
            return "redirect:/emprunts";
        } catch (IllegalStateException e) {
            model.addAttribute("emprunt", emprunt);
            model.addAttribute("livres", livreRepository.findAll());
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
            MvtProlongementEntity dernierMvt =
                mvtProlongementRepository.findAll().stream()
                    .filter(m -> m.getProlongement().getId().equals(prolongement.getId()))
                    .max(Comparator.comparing(MvtProlongementEntity::getDateMouvement))
                    .orElse(null);
            if (dernierMvt != null && "Valide".equalsIgnoreCase(dernierMvt.getStatutNouveau().getCodeStatut())) {
                datePrevue = prolongement.getDateFin();
            }
        }
        if (dateRetour.isAfter(datePrevue)) {
            PenaliteEntity penalite = new PenaliteEntity();
            penalite.setEmprunt(emprunt);
            penalite.setAdherent(emprunt.getAdherent());
            java.time.LocalDate dateDebutPenalite = dateRetour.toLocalDate();
            PenaliteEntity dernierePenalite = penaliteService
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
    public String mesEmprunts(Model model, @RequestParam(value = "success", required = false) String success, HttpSession session) {
        UtilisateurEntity utilisateur = (UtilisateurEntity) session.getAttribute("user");
        if (utilisateur == null) {
            model.addAttribute("error", "Vous devez être connecté pour voir vos emprunts.");
            return "pages/client/mes_emprunts";
        }
        AdherentEntity adherent = adherentRepository.findByUtilisateurId(utilisateur.getId());
        if (adherent == null) {
            model.addAttribute("error", "Aucun profil adhérent trouvé pour cet utilisateur.");
            return "pages/client/mes_emprunts";
        }
        List<EmpruntEntity> emprunts = empruntService.findByAdherentId(adherent.getId());
        Map<Long, String> statuts = new HashMap<>();
        Map<Long, java.time.LocalDateTime> dernieresProlongations = new HashMap<>();
        for (EmpruntEntity e : emprunts) {
            statuts.put(e.getId(), empruntService.getLastStatutForEmprunt(e.getId()));
            ProlongementEntity prolongement = prolongementRepository.findTopByEmpruntIdOrderByDateFinDesc(e.getId());
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
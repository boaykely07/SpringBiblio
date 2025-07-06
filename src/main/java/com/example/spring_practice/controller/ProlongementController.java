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
import com.example.spring_practice.model.entities.MvtProlongementEntity;
import com.example.spring_practice.model.entities.StatutProlongementEntity;
import com.example.spring_practice.repository.MvtProlongementRepository;
import com.example.spring_practice.repository.StatutProlongementRepository;
import com.example.spring_practice.repository.AdherentRepository;
import com.example.spring_practice.model.entities.AdherentEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import jakarta.servlet.http.HttpSession;

import java.util.List;
import java.util.Optional;
import java.time.LocalDateTime;
import java.util.Map;

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
    private MvtProlongementRepository mvtProlongementRepository;
    @Autowired
    private StatutProlongementRepository statutProlongementRepository;
    @Autowired
    private AdherentRepository adherentRepository;

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

    // Liste des prolongements du client connecté
    @GetMapping("/client")
    public String listProlongementsClient(Model model) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String email = auth.getName();
        AdherentEntity adherent = adherentRepository.findAll().stream()
            .filter(a -> a.getUtilisateur() != null && email.equals(a.getUtilisateur().getEmail()))
            .findFirst().orElse(null);
        if (adherent == null) {
            model.addAttribute("prolongements", List.of());
            model.addAttribute("activePage_client", "prolongements");
            return "pages/client/prolongement_list";
        }
        List<ProlongementEntity> prolongements = prolongementService.findAll().stream()
            .filter(p -> p.getEmprunt() != null && p.getEmprunt().getAdherent() != null &&
                p.getEmprunt().getAdherent().getId().equals(adherent.getId()))
            .toList();
        // Pour chaque prolongement, on peut ajouter le statut courant (le dernier mouvement)
        List<Map<String, Object>> prolongementsAvecStatut = prolongements.stream().map(p -> {
            Map<String, Object> map = new java.util.HashMap<>();
            map.put("id", p.getId());
            map.put("emprunt", p.getEmprunt());
            map.put("dateFin", p.getDateFin());
            map.put("dateProlongement", p.getDateProlongement());
            // Chercher le dernier mouvement
            MvtProlongementEntity mvt = mvtProlongementRepository.findAll().stream()
                .filter(m -> m.getProlongement().getId().equals(p.getId()))
                .max(java.util.Comparator.comparing(MvtProlongementEntity::getDateMouvement))
                .orElse(null);
            map.put("statut", mvt != null ? mvt.getStatutNouveau().getCodeStatut() : "-");
            return map;
        }).toList();
        model.addAttribute("prolongements", prolongementsAvecStatut);
        model.addAttribute("activePage_client", "prolongements");
        return "pages/client/prolongement_list";
    }

    // Formulaire d'ajout de prolongement (client)
    @GetMapping("/client/new")
    public String newProlongementFormClient(Model model, HttpSession session) {
        // Récupérer l'utilisateur connecté depuis la session
        com.example.spring_practice.model.entities.UtilisateurEntity utilisateur = (com.example.spring_practice.model.entities.UtilisateurEntity) session.getAttribute("user");
        if (utilisateur == null) {
            model.addAttribute("prolongement", new ProlongementEntity());
            model.addAttribute("emprunts", java.util.List.of());
            model.addAttribute("activePage_client", "prolongements");
            model.addAttribute("debugEmpruntsCount", 0);
            return "pages/client/prolongement_form";
        }
        AdherentEntity adherent = adherentRepository.findAll().stream()
            .filter(a -> a.getUtilisateur() != null && a.getUtilisateur().getId().equals(utilisateur.getId()))
            .findFirst().orElse(null);
        System.out.println("[DEBUG] Utilisateur connecté id=" + utilisateur.getId() + ", email=" + utilisateur.getEmail());
        System.out.println("[DEBUG] Adhérent trouvé : " + (adherent != null ? adherent.getNom() : "Aucun"));
        List<EmpruntEntity> emprunts = prolongementService.getEmpruntsProlongeablesPourAdherent(adherent);
        model.addAttribute("prolongement", new ProlongementEntity());
        model.addAttribute("emprunts", emprunts);
        model.addAttribute("activePage_client", "prolongements");
        model.addAttribute("debugEmpruntsCount", emprunts.size());
        return "pages/client/prolongement_form";
    }

    // Traitement de la demande de prolongement (client)
    @PostMapping("/client/new")
    public String saveProlongementClient(@ModelAttribute ProlongementEntity prolongement, Model model) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String email = auth.getName();
        AdherentEntity adherent = adherentRepository.findAll().stream()
            .filter(a -> a.getUtilisateur() != null && email.equals(a.getUtilisateur().getEmail()))
            .findFirst().orElse(null);
        if (adherent == null) {
            model.addAttribute("error", "Utilisateur non trouvé.");
            model.addAttribute("activePage_client", "prolongements");
            return "pages/client/prolongement_form";
        }
        // Vérifier que l'emprunt appartient au client
        if (prolongement.getEmprunt() == null || prolongement.getEmprunt().getId() == null) {
            model.addAttribute("error", "Veuillez sélectionner un emprunt valide.");
            model.addAttribute("activePage_client", "prolongements");
            return "pages/client/prolongement_form";
        }
        EmpruntEntity emprunt = empruntRepository.findById(prolongement.getEmprunt().getId()).orElse(null);
        if (emprunt == null || emprunt.getAdherent() == null || !emprunt.getAdherent().getId().equals(adherent.getId())) {
            model.addAttribute("error", "Emprunt non valide.");
            model.addAttribute("activePage_client", "prolongements");
            return "pages/client/prolongement_form";
        }
        // Vérifier la date
        if (prolongement.getDateFin() == null || !prolongement.getDateFin().isAfter(emprunt.getDateRetourPrevue())) {
            model.addAttribute("error", "La nouvelle date de fin doit être postérieure à la date de fin actuelle de l'emprunt.");
            model.addAttribute("activePage_client", "prolongements");
            return "pages/client/prolongement_form";
        }
        prolongement.setEmprunt(emprunt);
        prolongement.setDateProlongement(java.time.LocalDateTime.now());
        ProlongementEntity savedProlongement = prolongementService.save(prolongement);
        // Créer le mouvement en statut 'En attente'
        StatutProlongementEntity statutEnAttente = statutProlongementRepository.findByCodeStatut("En attente").orElse(null);
        if (statutEnAttente != null) {
            MvtProlongementEntity mvt = new MvtProlongementEntity();
            mvt.setProlongement(savedProlongement);
            mvt.setStatutNouveau(statutEnAttente);
            mvt.setDateMouvement(java.time.LocalDateTime.now());
            mvtProlongementRepository.save(mvt);
        }
        return "redirect:/prolongements/client";
    }
}

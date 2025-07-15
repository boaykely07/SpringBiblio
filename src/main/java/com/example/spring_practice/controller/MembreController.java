package com.example.spring_practice.controller;

import com.example.spring_practice.model.entities.MembreForm;
import com.example.spring_practice.service.AdherentService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import com.example.spring_practice.model.entities.AdherentEntity;
import com.example.spring_practice.model.entities.ProfilAdherentEntity;
import com.example.spring_practice.model.entities.AbonnementEntity;
import com.example.spring_practice.model.entities.PenaliteEntity;
import com.example.spring_practice.repository.AbonnementRepository;
import com.example.spring_practice.repository.PenaliteRepository;
import com.example.spring_practice.repository.EmpruntRepository;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.beans.factory.annotation.Autowired;
import java.util.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import org.springframework.web.bind.annotation.PathVariable;

@Controller
public class MembreController {

    private final AdherentService adherentService;

    @Autowired
    private AbonnementRepository abonnementRepository;
    @Autowired
    private PenaliteRepository penaliteRepository;
    @Autowired
    private EmpruntRepository empruntRepository;

    public MembreController(AdherentService adherentService) {
        this.adherentService = adherentService;
    }

    @GetMapping("/membres")
    public String listMembres(Model model) {
        model.addAttribute("membres", adherentService.findAll());
        model.addAttribute("activePage", "membres");
        return "pages/admin/membre_list";
    }

    @GetMapping("/membres/new")
    public String showMembreForm(Model model) {
        model.addAttribute("membreForm", new MembreForm());
        return "pages/admin/membre_form";
    }

    @PostMapping("/membres")
    public String createMembre(@ModelAttribute("membreForm") MembreForm membreForm, Model model) {
        String error = adherentService.inscrireMembre(membreForm);
        if (error != null) {
            model.addAttribute("error", error);
            return "pages/admin/membre_form";
        }
        return "redirect:/membres";
    }

    @GetMapping("/membres/api/{id}")
    @ResponseBody
    public Map<String, Object> getMembreDetailsApi(@PathVariable Long id) {
        Map<String, Object> result = new HashMap<>();
        Optional<AdherentEntity> optAdh = adherentService.findAll().stream().filter(a -> a.getId().equals(id)).findFirst();
        if (optAdh.isEmpty()) {
            result.put("error", "Membre introuvable");
            return result;
        }
        AdherentEntity adherent = optAdh.get();
        result.put("id", adherent.getId());
        result.put("nom", adherent.getNom());
        result.put("prenom", adherent.getPrenom());
        result.put("dateNaissance", adherent.getDateNaissance());
        // Profil adhérent
        ProfilAdherentEntity profil = adherent.getProfil();
        if (profil != null) {
            Map<String, Object> profilMap = new HashMap<>();
            profilMap.put("id", profil.getId());
            profilMap.put("nomProfil", profil.getNomProfil());
            profilMap.put("quotaEmpruntsSimultanes", profil.getQuotaEmpruntsSimultanes());
            profilMap.put("quotaPenalitesJours", profil.getQuotaPenalitesJours());
            result.put("profilAdherent", profilMap);
        }
        // Quota restant
        long empruntsActifs = empruntRepository.findByAdherentId(adherent.getId()).stream()
            .filter(e -> e.getDateRetourReelle() == null)
            .count();
        int quota = profil != null ? profil.getQuotaEmpruntsSimultanes() : 0;
        result.put("quotaRestant", Math.max(0, quota - (int)empruntsActifs));
        // Liste des abonnements
        List<AbonnementEntity> abonnements = abonnementRepository.findByAdherentId(adherent.getId());
        List<Map<String, Object>> abonnementsList = new ArrayList<>();
        for (AbonnementEntity ab : abonnements) {
            Map<String, Object> abMap = new HashMap<>();
            abMap.put("id", ab.getId());
            abMap.put("dateDebut", ab.getDateDebut());
            abMap.put("dateFin", ab.getDateFin());
            abonnementsList.add(abMap);
        }
        result.put("abonnements", abonnementsList);
        // Pénalité en cours (à la date du jour)
        List<PenaliteEntity> penalites = penaliteRepository.findByAdherentId(adherent.getId());
        LocalDate today = LocalDate.now();
        PenaliteEntity penaliteEnCours = penalites.stream()
            .filter(p -> !today.isBefore(p.getDateDebut()) && !today.isAfter(p.getDateDebut().plusDays(p.getJour() - 1)))
            .findFirst().orElse(null);
        if (penaliteEnCours != null) {
            Map<String, Object> penMap = new HashMap<>();
            penMap.put("id", penaliteEnCours.getId());
            penMap.put("dateDebut", penaliteEnCours.getDateDebut());
            penMap.put("jour", penaliteEnCours.getJour());
            penMap.put("raison", penaliteEnCours.getRaison());
            penMap.put("dateFin", penaliteEnCours.getDateDebut().plusDays(penaliteEnCours.getJour() - 1));
            result.put("penaliteEnCours", penMap);
        }
        // Toutes les pénalités (dates début/fin)
        List<Map<String, Object>> penalitesList = new ArrayList<>();
        for (PenaliteEntity p : penalites) {
            Map<String, Object> pMap = new HashMap<>();
            pMap.put("id", p.getId());
            pMap.put("dateDebut", p.getDateDebut());
            pMap.put("jour", p.getJour());
            pMap.put("raison", p.getRaison());
            pMap.put("dateFin", p.getDateDebut().plusDays(p.getJour() - 1));
            penalitesList.add(pMap);
        }
        result.put("penalites", penalitesList);
        return result;
    }
}

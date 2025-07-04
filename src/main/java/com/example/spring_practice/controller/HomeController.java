package com.example.spring_practice.controller;

import com.example.spring_practice.model.entities.UtilisateurEntity;
import com.example.spring_practice.repository.BibliothecaireRepository;
import com.example.spring_practice.repository.LivreRepository;
import com.example.spring_practice.repository.AdherentRepository;
import com.example.spring_practice.repository.EmpruntRepository;
import com.example.spring_practice.repository.MvtEmpruntRepository;
import com.example.spring_practice.repository.StatutEmpruntRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.ui.Model;

import jakarta.servlet.http.HttpSession;

@Controller
public class HomeController {
    @Autowired
    private BibliothecaireRepository bibliothecaireRepository;
    @Autowired
    private LivreRepository livreRepository;
    @Autowired
    private AdherentRepository adherentRepository;
    @Autowired
    private EmpruntRepository empruntRepository;
    @Autowired
    private MvtEmpruntRepository mvtEmpruntRepository;
    @Autowired
    private StatutEmpruntRepository statutEmpruntRepository;

    @GetMapping("/home")
    public String home(HttpSession session, Model model) {
        UtilisateurEntity utilisateur = (UtilisateurEntity) session.getAttribute("user");
        if (utilisateur == null) {
            return "redirect:/login";
        }
        boolean isBiblio = bibliothecaireRepository.findAll().stream()
                .anyMatch(b -> b.getUtilisateur().getId().equals(utilisateur.getId()));
        if (isBiblio) {
            // Statistiques dynamiques
            long totalLivres = livreRepository.count();
            long totalAdherents = adherentRepository.count();
            long empruntsActifs = empruntRepository.findAll().stream()
                .filter(e -> {
                    return mvtEmpruntRepository.findTopByEmpruntIdOrderByDateMouvementDesc(e.getId())
                        .map(mvt -> {
                            String statut = mvt.getStatutNouveau().getCodeStatut();
                            return "En cours".equalsIgnoreCase(statut);
                        }).orElse(false);
                }).count();
            long retards = empruntRepository.findAll().stream()
                .filter(e -> {
                    return mvtEmpruntRepository.findTopByEmpruntIdOrderByDateMouvementDesc(e.getId())
                        .map(mvt -> {
                            String statut = mvt.getStatutNouveau().getCodeStatut();
                            return "Retard".equalsIgnoreCase(statut);
                        }).orElse(false);
                }).count();
            model.addAttribute("totalLivres", totalLivres);
            model.addAttribute("totalAdherents", totalAdherents);
            model.addAttribute("empruntsActifs", empruntsActifs);
            model.addAttribute("retards", retards);
            return "pages/admin/home_bibliothecaire";
        } else {
            return "pages/home_client";
        }
    }
}

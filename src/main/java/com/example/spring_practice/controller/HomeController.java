package com.example.spring_practice.controller;

import com.example.spring_practice.model.entities.UtilisateurEntity;
import com.example.spring_practice.repository.BibliothecaireRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

import jakarta.servlet.http.HttpSession;

@Controller
public class HomeController {
    @Autowired
    private BibliothecaireRepository bibliothecaireRepository;

    @GetMapping("/home")
    public String home(HttpSession session) {
        UtilisateurEntity utilisateur = (UtilisateurEntity) session.getAttribute("user");
        if (utilisateur == null) {
            return "redirect:/login";
        }
        boolean isBiblio = bibliothecaireRepository.findAll().stream()
                .anyMatch(b -> b.getUtilisateur().getId().equals(utilisateur.getId()));
        if (isBiblio) {
            return "pages/admin/home_bibliothecaire";
        } else {
            return "pages/home_client";
        }
    }
}

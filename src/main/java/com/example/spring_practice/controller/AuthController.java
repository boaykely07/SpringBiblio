package com.example.spring_practice.controller;

import com.example.spring_practice.model.entities.UtilisateurEntity;
import com.example.spring_practice.model.entities.ProfilAdherentEntity;
import com.example.spring_practice.service.AuthService;
import com.example.spring_practice.repository.BibliothecaireRepository;
import com.example.spring_practice.repository.ProfilAdherentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpSession;
import java.time.LocalDate;
import java.util.List;

@Controller
public class AuthController {
    @Autowired
    private AuthService authService;

    @Autowired
    private BibliothecaireRepository bibliothecaireRepository;

    @Autowired
    private ProfilAdherentRepository profilAdherentRepository;

    @GetMapping("/register")
    public String showRegisterForm(Model model) {
        List<ProfilAdherentEntity> profils = profilAdherentRepository.findAll();
        model.addAttribute("profils", profils);
        return "pages/auth/register";
    }

    @PostMapping("/registerUser")
    public String register(@RequestParam String email,
            @RequestParam String password,
            @RequestParam String nom,
            @RequestParam String prenom,
            @RequestParam(required = false) String dateNaissance,
            @RequestParam(required = false) Long idProfil,
            @RequestParam String type,
            Model model) {
        try {
            if ("BIBLIOTHECAIRE".equalsIgnoreCase(type)) {
                authService.registerBibliothecaire(email, password, nom, prenom);
            } else {
                LocalDate naissance = LocalDate.parse(dateNaissance);
                authService.registerClient(email, password, nom, prenom, naissance, idProfil);
            }
            return "pages/auth/login";
        } catch (Exception e) {
            List<ProfilAdherentEntity> profils = profilAdherentRepository.findAll();
            model.addAttribute("profils", profils);
            model.addAttribute("error", e.getMessage());
            return "pages/auth/register";
        }
    }

    @PostMapping("/login")
    public String login(@RequestParam String email,
            @RequestParam String password,
            HttpSession session,
            Model model) {
        try {
            UtilisateurEntity utilisateur = authService.login(email, password);
            System.out.println("Authentification réussie pour l'utilisateur ID : " + utilisateur.getId());
            session.setAttribute("user", utilisateur);
            boolean isBiblio = bibliothecaireRepository.findAll().stream()
                    .anyMatch(b -> b.getUtilisateur().getId().equals(utilisateur.getId()));
            System.out.println("Type d'utilisateur : " + (isBiblio ? "BIBLIOTHECAIRE" : "CLIENT"));
            return "redirect:/home";
        } catch (Exception e) {
            System.out.println("Erreur lors de l'authentification : " + e.getMessage());
            throw e; // Pour voir la stacktrace complète dans la console
        }
    }

    @GetMapping("/logout")
    public String logout(HttpSession session) {
        session.invalidate();
        return "/pages/auth/login";
    }



    @GetMapping("/login")
    public String showLoginForm(@RequestParam(value = "error", required = false) String error, Model model) {
        if (error != null) {
            model.addAttribute("error", "Email ou mot de passe incorrect.");
        }
        return "pages/auth/login";
    }

}

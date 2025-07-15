package com.example.spring_practice.controller;

import com.example.spring_practice.model.entities.ProfilAdherentEntity;
import com.example.spring_practice.service.ProfilAdherentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

@Controller
public class ProfilAdherentController {
    @Autowired
    private ProfilAdherentService profilAdherentService;

    @GetMapping("/profils-adherent/list")
    public String listProfilsAdherent(Model model) {
        List<ProfilAdherentEntity> profils = profilAdherentService.findAll();
        model.addAttribute("profils", profils);
        model.addAttribute("activePage", "profils-adherent");
        return "pages/admin/profil_adherent_list";
    }

    @GetMapping("/profils-adherent/new")
    public String showCreateForm(Model model) {
        model.addAttribute("profilAdherent", new ProfilAdherentEntity());
        model.addAttribute("activePage", "profils-adherent");
        return "pages/admin/profil_adherent_form";
    }

    @org.springframework.web.bind.annotation.PostMapping("/profils-adherent/new")
    public String createProfilAdherent(@org.springframework.web.bind.annotation.ModelAttribute ProfilAdherentEntity profilAdherent) {
        profilAdherentService.save(profilAdherent);
        return "redirect:/profils-adherent/list";
    }

    @GetMapping("/profils-adherent/edit/{id}")
    public String showEditForm(@PathVariable Long id, Model model) {
        ProfilAdherentEntity profil = profilAdherentService.findById(id);
        model.addAttribute("profilAdherent", profil);
        model.addAttribute("activePage", "profils-adherent");
        return "pages/admin/profil_adherent_form";
    }

    @org.springframework.web.bind.annotation.PostMapping("/profils-adherent/edit/{id}")
    public String updateProfilAdherent(@PathVariable Long id, @org.springframework.web.bind.annotation.ModelAttribute ProfilAdherentEntity profilAdherent) {
        profilAdherent.setId(id);
        profilAdherentService.save(profilAdherent);
        return "redirect:/profils-adherent/list";
    }
} 
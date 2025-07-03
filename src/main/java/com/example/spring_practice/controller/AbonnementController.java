package com.example.spring_practice.controller;

import com.example.spring_practice.model.entities.AbonnementEntity;
import com.example.spring_practice.model.entities.AdherentEntity;
import com.example.spring_practice.service.AbonnementService;
import com.example.spring_practice.repository.AdherentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/abonnements")
public class AbonnementController {
    @Autowired
    private AbonnementService abonnementService;
    @Autowired
    private AdherentRepository adherentRepository;

    @GetMapping
    public String listAbonnements(Model model) {
        List<AbonnementEntity> abonnements = abonnementService.findAll();
        model.addAttribute("abonnements", abonnements);
        return "pages/admin/abonnement_list";
    }

    @GetMapping("/new")
    public String newAbonnementForm(Model model) {
        model.addAttribute("abonnement", new AbonnementEntity());
        model.addAttribute("adherents", adherentRepository.findAll());
        return "pages/admin/abonnement_form";
    }

    @PostMapping
    public String saveAbonnement(@ModelAttribute AbonnementEntity abonnement) {
        abonnementService.save(abonnement);
        return "redirect:/abonnements";
    }

    @GetMapping("/delete/{id}")
    public String deleteAbonnement(@PathVariable Long id) {
        abonnementService.deleteById(id);
        return "redirect:/abonnements";
    }
}

package com.example.spring_practice.controller;

import com.example.spring_practice.model.entities.MembreForm;
import com.example.spring_practice.service.AdherentService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
public class MembreController {

    private final AdherentService adherentService;

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
}

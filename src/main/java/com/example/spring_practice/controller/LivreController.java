package com.example.spring_practice.controller;

import com.example.spring_practice.model.entities.LivreEntity;
import com.example.spring_practice.service.LivreService;
import com.example.spring_practice.repository.LivreRepository;
import com.example.spring_practice.model.entities.EditeurEntity;
import com.example.spring_practice.repository.EditeurRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ModelAttribute;

import java.util.List;

@Controller
@RequestMapping("/livres")
public class LivreController {
    @Autowired
    private LivreService livreService;

    @Autowired
    private LivreRepository livreRepository;

    @Autowired
    private EditeurRepository editeurRepository;

    @GetMapping
    public String listLivres(Model model) {
        model.addAttribute("livres", livreRepository.findAll());
        model.addAttribute("activePage", "livres");
        return "pages/admin/livre_list";
    }

    @GetMapping("/reserver/{id}")
    public String reserverLivre(@PathVariable Long id, Model model) {
        LivreEntity livre = livreService.findById(id);
        model.addAttribute("livre", livre);
        return "pages/client/reserver_client";
    }

    @GetMapping("/new")
    public String newLivreForm(Model model) {
        model.addAttribute("livre", new LivreEntity());
        model.addAttribute("editeurs", editeurRepository.findAll());
        model.addAttribute("activePage", "livres");
        return "pages/admin/livre_form";
    }

    @PostMapping
    public String saveLivre(@ModelAttribute LivreEntity livre, Model model) {
        livreService.save(livre);
        return "redirect:/livres";
    }
} 
package com.example.spring_practice.controller;

import com.example.spring_practice.model.entities.EmpruntEntity;
import com.example.spring_practice.service.EmpruntService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import com.example.spring_practice.model.entities.ExemplaireEntity;
import com.example.spring_practice.model.entities.AdherentEntity;
import com.example.spring_practice.model.entities.TypeEmpruntEntity;
import com.example.spring_practice.repository.ExemplaireRepository;
import com.example.spring_practice.repository.AdherentRepository;
import com.example.spring_practice.repository.TypeEmpruntRepository;

import java.util.List;
import java.util.Optional;

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

    @GetMapping
    public String listEmprunts(Model model) {
        List<EmpruntEntity> emprunts = empruntService.findAll();
        model.addAttribute("emprunts", emprunts);
        return "emprunts/list";
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
        model.addAttribute("exemplaires", exemplaireRepository.findAll());
        model.addAttribute("adherents", adherentRepository.findAll());
        model.addAttribute("typesEmprunt", typeEmpruntRepository.findAll());
        return "pages/admin/emprunts_form";
    }

    @PostMapping
    public String saveEmprunt(@ModelAttribute EmpruntEntity emprunt) {
        empruntService.save(emprunt);
        return "redirect:/emprunts";
    }

    @GetMapping("/delete/{id}")
    public String deleteEmprunt(@PathVariable Long id) {
        empruntService.deleteById(id);
        return "redirect:/emprunts";
    }
} 
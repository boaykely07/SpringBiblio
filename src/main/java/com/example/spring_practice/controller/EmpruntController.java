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
import java.util.Map;
import java.util.HashMap;

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
        Map<Long, String> statuts = new HashMap<>();
        for (EmpruntEntity e : emprunts) {
            statuts.put(e.getId(), empruntService.getLastStatutForEmprunt(e.getId()));
        }
        model.addAttribute("emprunts", emprunts);
        model.addAttribute("statuts", statuts);
        return "pages/admin/list_emprunt";
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
    public String saveEmprunt(@ModelAttribute EmpruntEntity emprunt, Model model) {
        try {
            // Recharge l'exemplaire complet depuis la base
            if (emprunt.getExemplaire() != null && emprunt.getExemplaire().getId() != null) {
                ExemplaireEntity ex = exemplaireRepository.findById(emprunt.getExemplaire().getId())
                    .orElse(null);
                emprunt.setExemplaire(ex);
            }
            empruntService.save(emprunt);
            return "redirect:/emprunts";
        } catch (IllegalStateException e) {
            model.addAttribute("emprunt", emprunt);
            model.addAttribute("exemplaires", exemplaireRepository.findAll());
            model.addAttribute("adherents", adherentRepository.findAll());
            model.addAttribute("typesEmprunt", typeEmpruntRepository.findAll());
            model.addAttribute("error", e.getMessage());
            return "pages/admin/emprunts_form";
        }
    }

    @GetMapping("/delete/{id}")
    public String deleteEmprunt(@PathVariable Long id) {
        empruntService.deleteById(id);
        return "redirect:/emprunts";
    }

    @GetMapping("/return/{id}")
    public String returnEmprunt(@PathVariable Long id) {
        empruntService.returnEmprunt(id);
        return "redirect:/emprunts";
    }
} 
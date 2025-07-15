package com.example.spring_practice.controller;

import com.example.spring_practice.model.entities.LivreEntity;
import com.example.spring_practice.service.LivreService;
import com.example.spring_practice.repository.LivreRepository;
import com.example.spring_practice.model.entities.EditeurEntity;
import com.example.spring_practice.repository.EditeurRepository;
import com.example.spring_practice.repository.AuteurRepository;
import com.example.spring_practice.repository.CategorieRepository;
import com.example.spring_practice.repository.ExemplaireRepository;
import com.example.spring_practice.model.entities.ExemplaireEntity;
import com.example.spring_practice.model.entities.EmpruntEntity;
import com.example.spring_practice.model.entities.AdherentEntity;
import com.example.spring_practice.model.entities.ProlongementEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.web.bind.annotation.ResponseBody;
import java.util.*;
import com.example.spring_practice.repository.EmpruntRepository;

@Controller
@RequestMapping("/livres")
public class LivreController {
    @Autowired
    private LivreService livreService;

    @Autowired
    private LivreRepository livreRepository;

    @Autowired
    private EditeurRepository editeurRepository;

    @Autowired
    private AuteurRepository auteurRepository;

    @Autowired
    private CategorieRepository categorieRepository;

    @Autowired
    private ExemplaireRepository exemplaireRepository;

    @Autowired
    private EmpruntRepository empruntRepository;

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
        model.addAttribute("auteurs", auteurRepository.findAll());
        model.addAttribute("categories", categorieRepository.findAll());
        model.addAttribute("activePage", "livres");
        return "pages/admin/livre_form";
    }

    @PostMapping
    public String saveLivre(@ModelAttribute LivreEntity livre, HttpServletRequest request, Model model, @RequestParam("quantiteExemplaires") int quantiteExemplaires) {
        LivreEntity savedLivre = livreService.save(livre);
        for (int i = 0; i < quantiteExemplaires; i++) {
            ExemplaireEntity exemplaire = new ExemplaireEntity();
            exemplaire.setLivre(savedLivre);
            exemplaireRepository.save(exemplaire);
        }
        return "redirect:/livres";
    }

    @GetMapping("/client/list")
    public String listLivresClient(@RequestParam(value = "titre", required = false) String titre, Model model) {
        List<LivreEntity> livres;
        if (titre != null && !titre.isEmpty()) {
            livres = livreService.findByTitreContainingIgnoreCase(titre);
        } else {
            livres = livreService.findAll();
        }
        model.addAttribute("livres", livres);
        model.addAttribute("titre", titre);
        model.addAttribute("activePage_client", "livres");
        return "pages/client/livre_list";
    }

    @GetMapping("/api/{id}")
    @ResponseBody
    public Map<String, Object> getLivreDetailsApi(@PathVariable Long id) {
        LivreEntity livre = livreService.findById(id);
        Map<String, Object> result = new HashMap<>();
        if (livre == null) {
            result.put("error", "Livre introuvable");
            return result;
        }
        result.put("id", livre.getId());
        result.put("titre", livre.getTitre());
        result.put("isbn", livre.getIsbn());
        result.put("anneePublication", livre.getAnneePublication());
        result.put("editeur", livre.getEditeur() != null ? livre.getEditeur().getNom() : null);
        result.put("auteur", livre.getAuteur() != null ? livre.getAuteur().getNom() + " " + livre.getAuteur().getPrenom() : null);
        // Chaque exemplaire physique est une ligne
        List<Map<String, Object>> exemplairesList = new ArrayList<>();
        int nbExemplaires = 0;
        for (ExemplaireEntity ex : exemplaireRepository.findAll()) {
            if (ex.getLivre().getId().equals(livre.getId())) {
                nbExemplaires++;
                List<EmpruntEntity> emprunts = empruntRepository.findByExemplaireId(ex.getId());
                boolean disponible = emprunts.stream().noneMatch(emp -> emp.getDateRetourReelle() == null);
                Map<String, Object> exMap = new HashMap<>();
                exMap.put("id", ex.getId());
                exMap.put("disponible", disponible);
                exMap.put("label", "Exemplaire " + nbExemplaires);
                exemplairesList.add(exMap);
            }
        }
        result.put("nbExemplaires", nbExemplaires);
        result.put("exemplaires", exemplairesList);
        return result;
    }
} 
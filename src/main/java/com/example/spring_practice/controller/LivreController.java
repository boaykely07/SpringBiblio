package com.example.spring_practice.controller;

import com.example.spring_practice.model.entities.LivreEntity;
import com.example.spring_practice.service.LivreService;
import com.example.spring_practice.repository.LivreRepository;
import com.example.spring_practice.model.entities.EditeurEntity;
import com.example.spring_practice.repository.EditeurRepository;
import com.example.spring_practice.repository.AuteurRepository;
import com.example.spring_practice.repository.CategorieRepository;
import com.example.spring_practice.repository.ExemplaireRepository;
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

    @Autowired
    private AuteurRepository auteurRepository;

    @Autowired
    private CategorieRepository categorieRepository;

    @Autowired
    private ExemplaireRepository exemplaireRepository;

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
    public String saveLivre(@ModelAttribute LivreEntity livre, HttpServletRequest request, Model model) {
        LivreEntity savedLivre = livreService.save(livre);
        // Récupérer la quantité d'exemplaires depuis le formulaire
        String quantiteStr = request.getParameter("quantiteExemplaires");
        int quantite = 1;
        try {
            quantite = Integer.parseInt(quantiteStr);
        } catch (Exception e) {
            quantite = 1;
        }
        // Créer l'exemplaire associé
        com.example.spring_practice.model.entities.ExemplaireEntity exemplaire = new com.example.spring_practice.model.entities.ExemplaireEntity();
        exemplaire.setLivre(savedLivre);
        exemplaire.setQuantite(quantite);
        exemplaireRepository.save(exemplaire);
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
} 
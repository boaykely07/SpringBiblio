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
import org.springframework.web.bind.annotation.ResponseBody;
import java.util.stream.Collectors;
import java.util.Map;
import java.util.HashMap;

import java.util.List;
import com.example.spring_practice.service.EmpruntService;

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
    private EmpruntService empruntService;

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

    @GetMapping("/{id}/exemplaires-json")
    @ResponseBody
    public List<Map<String, Object>> getExemplairesJson(@PathVariable Long id) {
        List<com.example.spring_practice.model.entities.ExemplaireEntity> exemplaires = exemplaireRepository.findByLivreId(id);
        List<Map<String, Object>> result = new java.util.ArrayList<>();
        for (com.example.spring_practice.model.entities.ExemplaireEntity ex : exemplaires) {
            com.example.spring_practice.model.entities.LivreEntity livre = ex.getLivre();
            Map<String, Object> map = new HashMap<>();
            map.put("id_exemplaire", ex.getId());
            map.put("id_livre", livre.getId());
            map.put("titre", livre.getTitre());
            map.put("isbn", livre.getIsbn());
            map.put("annee_publication", livre.getAnneePublication());
            map.put("resume", livre.getResume());
            // Auteur
            if (livre.getAuteur() != null) {
                Map<String, Object> auteur = new HashMap<>();
                auteur.put("id_auteur", livre.getAuteur().getId());
                auteur.put("nom", livre.getAuteur().getNom());
                auteur.put("prenom", livre.getAuteur().getPrenom());
                map.put("auteur", auteur);
            } else {
                map.put("auteur", null);
            }
            // Editeur
            if (livre.getEditeur() != null) {
                Map<String, Object> editeur = new HashMap<>();
                editeur.put("id_editeur", livre.getEditeur().getId());
                editeur.put("nom", livre.getEditeur().getNom());
                map.put("editeur", editeur);
            } else {
                map.put("editeur", null);
            }
            // Categories
            if (livre.getCategories() != null) {
                List<Map<String, Object>> categories = livre.getCategories().stream().map(cat -> {
                    Map<String, Object> catMap = new HashMap<>();
                    catMap.put("id_categorie", cat.getId());
                    catMap.put("nom", cat.getNom());
                    return catMap;
                }).collect(Collectors.toList());
                map.put("categories", categories);
            } else {
                map.put("categories", null);
            }
            // Etat de chaque exemplaire physique
            int quantite = ex.getQuantite();
            List<com.example.spring_practice.model.entities.EmpruntEntity> emprunts = empruntService.findAll().stream()
                .filter(emp -> emp.getExemplaire().getId().equals(ex.getId()))
                .collect(Collectors.toList());
            // On considère qu'il y a quantite exemplaires physiques, on va marquer les premiers N comme non disponibles si il y a N emprunts "En cours"
            int nbEmpruntsEnCours = (int) emprunts.stream()
                .filter(emp -> "En cours".equalsIgnoreCase(empruntService.getLastStatutForEmprunt(emp.getId())))
                .count();
            List<Map<String, Object>> exemplairesPhysiques = new java.util.ArrayList<>();
            for (int i = 1; i <= quantite; i++) {
                Map<String, Object> etat = new HashMap<>();
                etat.put("numero_exemplaire", i);
                etat.put("disponible", i > nbEmpruntsEnCours);
                exemplairesPhysiques.add(etat);
            }
            map.put("exemplaires_physiques", exemplairesPhysiques);
            // Debug : liste des emprunts et leur statut
            List<Map<String, Object>> empruntsDebug = emprunts.stream().map(emp -> {
                Map<String, Object> empMap = new HashMap<>();
                empMap.put("id_emprunt", emp.getId());
                empMap.put("date_emprunt", emp.getDateEmprunt());
                empMap.put("date_retour_prevue", emp.getDateRetourPrevue());
                empMap.put("date_retour_reelle", emp.getDateRetourReelle());
                empMap.put("statut", empruntService.getLastStatutForEmprunt(emp.getId()));
                return empMap;
            }).collect(Collectors.toList());
            map.put("emprunts_debug", empruntsDebug);
            result.add(map);
        }
        return result;
    }
} 
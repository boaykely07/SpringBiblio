package com.example.spring_practice.model.entities;

import jakarta.persistence.*;
import java.util.List;

@Entity
@Table(name = "Livres")
public class LivreEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_livre")
    private Long id;

    @Column(name = "titre", nullable = false)
    private String titre;

    @Column(name = "isbn", unique = true)
    private String isbn;

    @Column(name = "annee_publication")
    private Integer anneePublication;

    @Column(name = "resume")
    private String resume;

    @ManyToOne
    @JoinColumn(name = "id_editeur")
    private EditeurEntity editeur;

    @ManyToOne
    @JoinColumn(name = "id_auteur")
    private AuteurEntity auteur;

    @ManyToMany
    @JoinTable(
        name = "Livres_Categories",
        joinColumns = @JoinColumn(name = "id_livre"),
        inverseJoinColumns = @JoinColumn(name = "id_categorie")
    )
    private List<CategorieEntity> categories;

    // Getters et setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getTitre() { return titre; }
    public void setTitre(String titre) { this.titre = titre; }
    public String getIsbn() { return isbn; }
    public void setIsbn(String isbn) { this.isbn = isbn; }
    public Integer getAnneePublication() { return anneePublication; }
    public void setAnneePublication(Integer anneePublication) { this.anneePublication = anneePublication; }
    public String getResume() { return resume; }
    public void setResume(String resume) { this.resume = resume; }
    public EditeurEntity getEditeur() { return editeur; }
    public void setEditeur(EditeurEntity editeur) { this.editeur = editeur; }
    public AuteurEntity getAuteur() { return auteur; }
    public void setAuteur(AuteurEntity auteur) { this.auteur = auteur; }
    public List<CategorieEntity> getCategories() { return categories; }
    public void setCategories(List<CategorieEntity> categories) { this.categories = categories; }
} 
package com.example.spring_practice.model.entities;

import jakarta.persistence.*;

@Entity
@Table(name = "Bibliothecaires")
public class BibliothecaireEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    @JoinColumn(name = "id_utilisateur", referencedColumnName = "id", unique = true)
    private UtilisateurEntity utilisateur;

    @Column(nullable = false)
    private String nom;

    @Column(nullable = false)
    private String prenom;

    // Getters et setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public UtilisateurEntity getUtilisateur() {
        return utilisateur;
    }

    public void setUtilisateur(UtilisateurEntity utilisateur) {
        this.utilisateur = utilisateur;
    }

    public String getNom() {
        return nom;
    }

    public void setNom(String nom) {
        this.nom = nom;
    }

    public String getPrenom() {
        return prenom;
    }

    public void setPrenom(String prenom) {
        this.prenom = prenom;
    }
}
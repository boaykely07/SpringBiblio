package com.example.spring_practice.model.entities;

import jakarta.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "Adherents")
public class AdherentEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_adherent")
    private Long id;

    @OneToOne
    @JoinColumn(name = "id_utilisateur", referencedColumnName = "id", unique = true)
    private UtilisateurEntity utilisateur;

    @Column(nullable = false)
    private String nom;

    @Column(nullable = false)
    private String prenom;

    @Column(name = "date_naissance", nullable = false)
    private LocalDate dateNaissance;

    @Column(name = "date_inscription", nullable = false)
    private LocalDate dateInscription = LocalDate.now();

    @ManyToOne
    @JoinColumn(name = "id_profil", nullable = false)
    private ProfilAdherentEntity profil;

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

    public LocalDate getDateNaissance() {
        return dateNaissance;
    }

    public void setDateNaissance(LocalDate dateNaissance) {
        this.dateNaissance = dateNaissance;
    }

    public LocalDate getDateInscription() {
        return dateInscription;
    }

    public void setDateInscription(LocalDate dateInscription) {
        this.dateInscription = dateInscription;
    }

    public ProfilAdherentEntity getProfil() {
        return profil;
    }

    public void setProfil(ProfilAdherentEntity profil) {
        this.profil = profil;
    }
}
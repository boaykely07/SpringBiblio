package com.example.spring_practice.model.entities;

import jakarta.persistence.*;

@Entity
@Table(name = "Profils_Adherent")
public class ProfilAdherentEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_profil")
    private Long id;

    @Column(name = "nom_profil", nullable = false, unique = true)
    private String nomProfil;

    @Column(name = "quota_emprunts_simultanes", nullable = false)
    private int quotaEmpruntsSimultanes = 3;

    // Getters et setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNomProfil() {
        return nomProfil;
    }

    public void setNomProfil(String nomProfil) {
        this.nomProfil = nomProfil;
    }

    public int getQuotaEmpruntsSimultanes() {
        return quotaEmpruntsSimultanes;
    }

    public void setQuotaEmpruntsSimultanes(int quotaEmpruntsSimultanes) {
        this.quotaEmpruntsSimultanes = quotaEmpruntsSimultanes;
    }
}
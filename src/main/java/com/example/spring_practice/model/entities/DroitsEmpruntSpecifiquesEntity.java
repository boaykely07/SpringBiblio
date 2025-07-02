package com.example.spring_practice.model.entities;

import jakarta.persistence.*;

@Entity
@Table(name = "Droits_Emprunt_Specifiques")
public class DroitsEmpruntSpecifiquesEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_droit")
    private Long id;

    @ManyToOne
    @JoinColumn(name = "id_livre", nullable = false)
    private LivreEntity livre;

    @ManyToOne
    @JoinColumn(name = "id_profil", nullable = false)
    private ProfilAdherentEntity profil;

    @Column(name = "age")
    private Integer age;

    @Column(name = "emprunt_surplace_autorise", nullable = false)
    private boolean empruntSurplaceAutorise = true;

    @Column(name = "emprunt_domicile_autorise", nullable = false)
    private boolean empruntDomicileAutorise = true;

    // Getters et setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public LivreEntity getLivre() { return livre; }
    public void setLivre(LivreEntity livre) { this.livre = livre; }
    public ProfilAdherentEntity getProfil() { return profil; }
    public void setProfil(ProfilAdherentEntity profil) { this.profil = profil; }
    public Integer getAge() { return age; }
    public void setAge(Integer age) { this.age = age; }
    public boolean isEmpruntSurplaceAutorise() { return empruntSurplaceAutorise; }
    public void setEmpruntSurplaceAutorise(boolean empruntSurplaceAutorise) { this.empruntSurplaceAutorise = empruntSurplaceAutorise; }
    public boolean isEmpruntDomicileAutorise() { return empruntDomicileAutorise; }
    public void setEmpruntDomicileAutorise(boolean empruntDomicileAutorise) { this.empruntDomicileAutorise = empruntDomicileAutorise; }
} 
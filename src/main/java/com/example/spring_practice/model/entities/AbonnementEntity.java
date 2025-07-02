package com.example.spring_practice.model.entities;

import jakarta.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "Abonnements")
public class AbonnementEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_abonnement")
    private Long id;

    @ManyToOne
    @JoinColumn(name = "id_adherent", nullable = false)
    private AdherentEntity adherent;

    @Column(name = "date_debut", nullable = false)
    private LocalDate dateDebut;

    @Column(name = "date_fin", nullable = false)
    private LocalDate dateFin;

    // Getters et setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public AdherentEntity getAdherent() { return adherent; }
    public void setAdherent(AdherentEntity adherent) { this.adherent = adherent; }
    public LocalDate getDateDebut() { return dateDebut; }
    public void setDateDebut(LocalDate dateDebut) { this.dateDebut = dateDebut; }
    public LocalDate getDateFin() { return dateFin; }
    public void setDateFin(LocalDate dateFin) { this.dateFin = dateFin; }
} 
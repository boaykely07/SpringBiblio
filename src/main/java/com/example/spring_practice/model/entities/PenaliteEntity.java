package com.example.spring_practice.model.entities;

import jakarta.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "Penalites")
public class PenaliteEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_penalite")
    private Long id;

    @ManyToOne
    @JoinColumn(name = "id_emprunt", nullable = false)
    private EmpruntEntity emprunt;

    @ManyToOne
    @JoinColumn(name = "id_adherent", nullable = false)
    private AdherentEntity adherent;

    @Column(name = "date_debut", nullable = false)
    private LocalDate dateDebut;

    @Column(name = "jour", nullable = false)
    private int jour;

    @Column(name = "raison")
    private String raison;

    // Getters et setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public EmpruntEntity getEmprunt() { return emprunt; }
    public void setEmprunt(EmpruntEntity emprunt) { this.emprunt = emprunt; }
    public AdherentEntity getAdherent() { return adherent; }
    public void setAdherent(AdherentEntity adherent) { this.adherent = adherent; }
    public LocalDate getDateDebut() { return dateDebut; }
    public void setDateDebut(LocalDate dateDebut) { this.dateDebut = dateDebut; }
    public int getJour() { return jour; }
    public void setJour(int jour) { this.jour = jour; }
    public String getRaison() { return raison; }
    public void setRaison(String raison) { this.raison = raison; }
} 
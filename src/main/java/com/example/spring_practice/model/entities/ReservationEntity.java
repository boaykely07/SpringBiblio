package com.example.spring_practice.model.entities;

import jakarta.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "Reservations")
public class ReservationEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_reservation")
    private Long id;

    @ManyToOne
    @JoinColumn(name = "id_livre", nullable = false)
    private LivreEntity livre;

    @ManyToOne
    @JoinColumn(name = "id_adherent", nullable = false)
    private AdherentEntity adherent;

    @Column(name = "date_demande", nullable = false)
    private LocalDateTime dateDemande;

    @Column(name = "date_a_reserver", nullable = false)
    private LocalDate dateAReserver;

    // Getters et setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public LivreEntity getLivre() { return livre; }
    public void setLivre(LivreEntity livre) { this.livre = livre; }
    public AdherentEntity getAdherent() { return adherent; }
    public void setAdherent(AdherentEntity adherent) { this.adherent = adherent; }
    public LocalDateTime getDateDemande() { return dateDemande; }
    public void setDateDemande(LocalDateTime dateDemande) { this.dateDemande = dateDemande; }
    public LocalDate getDateAReserver() { return dateAReserver; }
    public void setDateAReserver(LocalDate dateAReserver) { this.dateAReserver = dateAReserver; }
} 
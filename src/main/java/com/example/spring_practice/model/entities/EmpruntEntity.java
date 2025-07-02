package com.example.spring_practice.model.entities;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "Emprunts")
public class EmpruntEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_emprunt")
    private Long id;

    @ManyToOne
    @JoinColumn(name = "id_exemplaire", nullable = false)
    private ExemplaireEntity exemplaire;

    @ManyToOne
    @JoinColumn(name = "id_adherent", nullable = false)
    private AdherentEntity adherent;

    @ManyToOne
    @JoinColumn(name = "id_type_emprunt", nullable = false)
    private TypeEmpruntEntity typeEmprunt;

    @Column(name = "date_emprunt", nullable = false)
    private LocalDateTime dateEmprunt;

    @Column(name = "date_retour_prevue", nullable = false)
    private LocalDateTime dateRetourPrevue;

    // Getters et setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public ExemplaireEntity getExemplaire() { return exemplaire; }
    public void setExemplaire(ExemplaireEntity exemplaire) { this.exemplaire = exemplaire; }
    public AdherentEntity getAdherent() { return adherent; }
    public void setAdherent(AdherentEntity adherent) { this.adherent = adherent; }
    public TypeEmpruntEntity getTypeEmprunt() { return typeEmprunt; }
    public void setTypeEmprunt(TypeEmpruntEntity typeEmprunt) { this.typeEmprunt = typeEmprunt; }
    public LocalDateTime getDateEmprunt() { return dateEmprunt; }
    public void setDateEmprunt(LocalDateTime dateEmprunt) { this.dateEmprunt = dateEmprunt; }
    public LocalDateTime getDateRetourPrevue() { return dateRetourPrevue; }
    public void setDateRetourPrevue(LocalDateTime dateRetourPrevue) { this.dateRetourPrevue = dateRetourPrevue; }
} 
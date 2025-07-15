package com.example.spring_practice.model.entities;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "Mvt_Prolongement")
public class MvtProlongementEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_mvt_prolongement")
    private Long id;

    @ManyToOne
    @JoinColumn(name = "id_prolongement", nullable = false)
    private ProlongementEntity prolongement;

    @ManyToOne
    @JoinColumn(name = "id_statut_nouveau", nullable = false)
    private StatutProlongementEntity statutNouveau;

    @Column(name = "date_mouvement", nullable = false)
    private LocalDateTime dateMouvement = LocalDateTime.now();

    // Getters et setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public ProlongementEntity getProlongement() { return prolongement; }
    public void setProlongement(ProlongementEntity prolongement) { this.prolongement = prolongement; }
    public StatutProlongementEntity getStatutNouveau() { return statutNouveau; }
    public void setStatutNouveau(StatutProlongementEntity statutNouveau) { this.statutNouveau = statutNouveau; }
    public LocalDateTime getDateMouvement() { return dateMouvement; }
    public void setDateMouvement(LocalDateTime dateMouvement) { this.dateMouvement = dateMouvement; }
} 
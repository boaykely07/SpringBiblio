package com.example.spring_practice.model.entities;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "Mvt_Emprunt")
public class MvtEmpruntEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_mvt_emprunt")
    private Long id;

    @ManyToOne
    @JoinColumn(name = "id_emprunt", nullable = false)
    private EmpruntEntity emprunt;

    @ManyToOne
    @JoinColumn(name = "id_statut_nouveau", nullable = false)
    private StatutEmpruntEntity statutNouveau;

    @Column(name = "date_mouvement", nullable = false)
    private LocalDateTime dateMouvement = LocalDateTime.now();

    // Getters et setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public EmpruntEntity getEmprunt() { return emprunt; }
    public void setEmprunt(EmpruntEntity emprunt) { this.emprunt = emprunt; }
    public StatutEmpruntEntity getStatutNouveau() { return statutNouveau; }
    public void setStatutNouveau(StatutEmpruntEntity statutNouveau) { this.statutNouveau = statutNouveau; }
    public LocalDateTime getDateMouvement() { return dateMouvement; }
    public void setDateMouvement(LocalDateTime dateMouvement) { this.dateMouvement = dateMouvement; }
} 
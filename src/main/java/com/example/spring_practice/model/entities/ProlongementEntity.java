package com.example.spring_practice.model.entities;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "Prolongements")
public class ProlongementEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_prolongement")
    private Long id;

    @ManyToOne
    @JoinColumn(name = "id_emprunt", nullable = false)
    private EmpruntEntity emprunt;

    @Column(name = "date_fin", nullable = false)
    private LocalDateTime dateFin;

    @Column(name = "date_prolongement", nullable = false)
    private LocalDateTime dateProlongement;

    // Getters et setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public EmpruntEntity getEmprunt() { return emprunt; }
    public void setEmprunt(EmpruntEntity emprunt) { this.emprunt = emprunt; }
    public LocalDateTime getDateFin() { return dateFin; }
    public void setDateFin(LocalDateTime dateFin) { this.dateFin = dateFin; }
    public LocalDateTime getDateProlongement() { return dateProlongement; }
    public void setDateProlongement(LocalDateTime dateProlongement) { this.dateProlongement = dateProlongement; }
} 
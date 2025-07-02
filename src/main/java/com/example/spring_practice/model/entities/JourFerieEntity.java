package com.example.spring_practice.model.entities;

import jakarta.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "Jours_Feries")
public class JourFerieEntity {
    @Id
    @Column(name = "date_ferie")
    private LocalDate dateFerie;

    @Column(name = "description")
    private String description;

    // Getters et setters
    public LocalDate getDateFerie() { return dateFerie; }
    public void setDateFerie(LocalDate dateFerie) { this.dateFerie = dateFerie; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
} 
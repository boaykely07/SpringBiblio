package com.example.spring_practice.model.entities;

import jakarta.persistence.*;

@Entity
@Table(name = "Type_emprunts")
public class TypeEmpruntEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_type_emprunt")
    private Long id;

    @Column(name = "nom_type", length = 50)
    private String nomType;

    // Getters et setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getNomType() { return nomType; }
    public void setNomType(String nomType) { this.nomType = nomType; }
} 
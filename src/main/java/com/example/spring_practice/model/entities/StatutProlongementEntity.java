package com.example.spring_practice.model.entities;

import jakarta.persistence.*;

@Entity
@Table(name = "Statuts_Prolongement")
public class StatutProlongementEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_statut")
    private Long id;

    @Column(name = "code_statut", nullable = false, unique = true)
    private String codeStatut;

    // Getters et setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getCodeStatut() { return codeStatut; }
    public void setCodeStatut(String codeStatut) { this.codeStatut = codeStatut; }
} 
package com.example.spring_practice.model.entities;

import jakarta.persistence.*;

@Entity
@Table(name = "Exemplaires")
public class ExemplaireEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_exemplaire")
    private Long id;

    @ManyToOne
    @JoinColumn(name = "id_livre", nullable = false)
    private LivreEntity livre;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public LivreEntity getLivre() { return livre; }
    public void setLivre(LivreEntity livre) { this.livre = livre; }
} 
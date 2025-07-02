package com.example.spring_practice.model.entities;

import jakarta.persistence.*;
import java.io.Serializable;

@Entity
@Table(name = "Livres_Auteurs")
@IdClass(LivreAuteurEntity.LivreAuteurId.class)
public class LivreAuteurEntity {
    @Id
    @Column(name = "id_livre")
    private Long idLivre;

    @Id
    @Column(name = "id_auteur")
    private Long idAuteur;

    @ManyToOne
    @JoinColumn(name = "id_livre", insertable = false, updatable = false)
    private LivreEntity livre;

    @ManyToOne
    @JoinColumn(name = "id_auteur", insertable = false, updatable = false)
    private AuteurEntity auteur;

    public static class LivreAuteurId implements Serializable {
        private Long idLivre;
        private Long idAuteur;
        // equals, hashCode
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            LivreAuteurId that = (LivreAuteurId) o;
            return idLivre.equals(that.idLivre) && idAuteur.equals(that.idAuteur);
        }
        public int hashCode() { return idLivre.hashCode() + idAuteur.hashCode(); }
    }
    // Getters et setters
    public Long getIdLivre() { return idLivre; }
    public void setIdLivre(Long idLivre) { this.idLivre = idLivre; }
    public Long getIdAuteur() { return idAuteur; }
    public void setIdAuteur(Long idAuteur) { this.idAuteur = idAuteur; }
    public LivreEntity getLivre() { return livre; }
    public void setLivre(LivreEntity livre) { this.livre = livre; }
    public AuteurEntity getAuteur() { return auteur; }
    public void setAuteur(AuteurEntity auteur) { this.auteur = auteur; }
} 
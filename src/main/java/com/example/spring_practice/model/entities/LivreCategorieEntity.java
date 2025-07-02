package com.example.spring_practice.model.entities;

import jakarta.persistence.*;
import java.io.Serializable;

@Entity
@Table(name = "Livres_Categories")
@IdClass(LivreCategorieEntity.LivreCategorieId.class)
public class LivreCategorieEntity {
    @Id
    @Column(name = "id_livre")
    private Long idLivre;

    @Id
    @Column(name = "id_categorie")
    private Long idCategorie;

    @ManyToOne
    @JoinColumn(name = "id_livre", insertable = false, updatable = false)
    private LivreEntity livre;

    @ManyToOne
    @JoinColumn(name = "id_categorie", insertable = false, updatable = false)
    private CategorieEntity categorie;

    public static class LivreCategorieId implements Serializable {
        private Long idLivre;
        private Long idCategorie;
        // equals, hashCode
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            LivreCategorieId that = (LivreCategorieId) o;
            return idLivre.equals(that.idLivre) && idCategorie.equals(that.idCategorie);
        }
        public int hashCode() { return idLivre.hashCode() + idCategorie.hashCode(); }
    }
    // Getters et setters
    public Long getIdLivre() { return idLivre; }
    public void setIdLivre(Long idLivre) { this.idLivre = idLivre; }
    public Long getIdCategorie() { return idCategorie; }
    public void setIdCategorie(Long idCategorie) { this.idCategorie = idCategorie; }
    public LivreEntity getLivre() { return livre; }
    public void setLivre(LivreEntity livre) { this.livre = livre; }
    public CategorieEntity getCategorie() { return categorie; }
    public void setCategorie(CategorieEntity categorie) { this.categorie = categorie; }
} 
package com.example.spring_practice.model.entities;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "Mvt_Reservation")
public class MvtReservationEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_mvt_reservation")
    private Long id;

    @ManyToOne
    @JoinColumn(name = "id_reservation", nullable = false)
    private ReservationEntity reservation;

    @ManyToOne
    @JoinColumn(name = "id_statut_nouveau", nullable = false)
    private StatutReservationEntity statutNouveau;

    @Column(name = "date_mouvement", nullable = false)
    private LocalDateTime dateMouvement;

    // Getters et setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public ReservationEntity getReservation() { return reservation; }
    public void setReservation(ReservationEntity reservation) { this.reservation = reservation; }
    public StatutReservationEntity getStatutNouveau() { return statutNouveau; }
    public void setStatutNouveau(StatutReservationEntity statutNouveau) { this.statutNouveau = statutNouveau; }
    public LocalDateTime getDateMouvement() { return dateMouvement; }
    public void setDateMouvement(LocalDateTime dateMouvement) { this.dateMouvement = dateMouvement; }
} 
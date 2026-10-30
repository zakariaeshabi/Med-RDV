package com.medrdv.rendezvous.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(
        name = "rendezvous",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uk_rdv_praticien_creneau",
                        columnNames = {"praticien_id", "date_heure"}
                ),
                @UniqueConstraint(
                        name = "uk_rdv_patient_creneau",
                        columnNames = {"patient_id", "date_heure"}
                )
        }
)
public class Rendezvous {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "patient_id", nullable = false)
    private Long patientId;

    @Column(name = "praticien_id", nullable = false)
    private Long praticienId;

    @Column(name = "date_heure", nullable = false)
    private LocalDateTime dateHeure;

    @Column(nullable = false, unique = true, length = 255)
    private String idempotencyKey;

    @Column(nullable = false, length = 30)
    private String statut;

    protected Rendezvous() {
    }

    public Rendezvous(
            Long patientId,
            Long praticienId,
            LocalDateTime dateHeure,
            String idempotencyKey) {

        this.patientId = patientId;
        this.praticienId = praticienId;
        this.dateHeure = dateHeure;
        this.idempotencyKey = idempotencyKey;
        this.statut = "CONFIRME";
    }

    public Long getId() {
        return id;
    }

    public Long getPatientId() {
        return patientId;
    }

    public Long getPraticienId() {
        return praticienId;
    }

    public LocalDateTime getDateHeure() {
        return dateHeure;
    }

    public String getIdempotencyKey() {
        return idempotencyKey;
    }

    public String getStatut() {
        return statut;
    }
}
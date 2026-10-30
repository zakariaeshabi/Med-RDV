package com.medrdv.servicepraticiens.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "praticiens")
public class Praticien {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 100)
    private String nom;

    @Column(nullable = false, length = 100)
    private String prenom;

    @Column(nullable = false, length = 100)
    private String specialite;

    @Column(nullable = false)
    private boolean actif;

    protected Praticien() {
    }

    public Praticien(
            String nom,
            String prenom,
            String specialite,
            boolean actif) {
        this.nom = nom;
        this.prenom = prenom;
        this.specialite = specialite;
        this.actif = actif;
    }

    public Long getId() {
        return id;
    }

    public String getNom() {
        return nom;
    }

    public String getPrenom() {
        return prenom;
    }

    public String getSpecialite() {
        return specialite;
    }

    public boolean isActif() {
        return actif;
    }
}
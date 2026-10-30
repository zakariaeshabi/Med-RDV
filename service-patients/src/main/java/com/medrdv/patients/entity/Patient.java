package com.medrdv.patients.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "patients")
public class Patient {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 100)
    private String nom;

    @Column(nullable = false, length = 100)
    private String prenom;

    @Column(nullable = false, length = 254)
    private String email;

    @Column(nullable = false)
    private boolean actif;

    protected Patient() {
    }

    public Patient(String nom, String prenom, String email, boolean actif) {
        this.nom = nom;
        this.prenom = prenom;
        this.email = email;
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

    public String getEmail() {
        return email;
    }

    public boolean isActif() {
        return actif;
    }
}
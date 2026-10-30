package com.medrdv.patients.dto;

public record PatientDto(
        Long id,
        String nom,
        String prenom,
        String email,
        boolean actif
) {
}
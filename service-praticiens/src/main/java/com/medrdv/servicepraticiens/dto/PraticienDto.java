package com.medrdv.servicepraticiens.dto;

public record PraticienDto(
        Long id,
        String nom,
        String prenom,
        String specialite,
        boolean actif
) {
}
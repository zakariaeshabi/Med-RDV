package com.medrdv.rendezvous.feign;

public record PatientDto(
        Long id,
        String nom,
        String prenom,
        String email,
        boolean actif
) {
}
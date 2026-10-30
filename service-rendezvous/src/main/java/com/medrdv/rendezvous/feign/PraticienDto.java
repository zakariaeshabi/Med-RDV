package com.medrdv.rendezvous.feign;

public record PraticienDto(
        Long id,
        String nom,
        String prenom,
        String specialite,
        boolean actif
) {
}
package com.medrdv.rendezvous.dto;

import java.time.LocalDateTime;

public record RendezvousDto(
        Long id,
        Long patientId,
        Long praticienId,
        LocalDateTime dateHeure,
        String statut
) {
}
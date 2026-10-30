package com.medrdv.rendezvous.dto;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.time.LocalDateTime;

public record CreateRendezvousRequest(

        @NotNull
        @Positive
        Long patientId,

        @NotNull
        @Positive
        Long praticienId,

        @NotNull
        @Future
        LocalDateTime dateHeure
) {
}
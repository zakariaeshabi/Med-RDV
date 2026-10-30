package com.medrdv.servicepraticiens.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record CreatePraticienRequest(

        @NotBlank
        @Size(max = 100)
        String nom,

        @NotBlank
        @Size(max = 100)
        String prenom,

        @NotBlank
        @Size(max = 100)
        String specialite,

        @NotNull
        Boolean actif
) {
}
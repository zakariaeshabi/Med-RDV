package com.medrdv.patients.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record CreatePatientRequest(

        @NotBlank
        @Size(max = 100)
        String nom,

        @NotBlank
        @Size(max = 100)
        String prenom,

        @NotBlank
        @Email
        @Size(max = 254)
        String email,

        @NotNull
        Boolean actif
) {
}
package com.medrdv.notifications.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record NotificationEmailDto(

        @NotBlank
        @Email
        @Size(max = 254)
        String destinataire,

        @NotBlank
        @Size(max = 200)
        String objet,

        @NotBlank
        @Size(max = 5000)
        String contenu
) {
}
package com.medrdv.rendezvous.messaging.event;

import java.util.UUID;

/**
 * Contrat JSON de notification e-mail Med-RDV, version 1.
 */
public record NotificationEmailRequestedEvent(
        UUID eventId,
        int eventVersion,
        String occurredAt,
        String correlationId,
        Long rendezvousId,
        String email,
        String sujet,
        String contenu
) {
}
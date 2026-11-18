package com.medrdv.notifications.messaging;

import com.medrdv.notifications.dto.NotificationEmailDto;
import com.medrdv.notifications.messaging.event.NotificationEmailRequestedEvent;
import com.medrdv.notifications.service.NotificationService;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Validator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

@Component
public class NotificationConsumer {

    private static final Logger log =
            LoggerFactory.getLogger(NotificationConsumer.class);

    private final NotificationService notificationService;
    private final Validator validator;

    public NotificationConsumer(
            NotificationService notificationService,
            Validator validator) {

        this.notificationService = notificationService;
        this.validator = validator;
    }

    @KafkaListener(topics = "${app.kafka.topic.notification}")
    public void recevoir(
            @Payload(required = false) NotificationEmailRequestedEvent event,
            @Header(KafkaHeaders.RECEIVED_PARTITION) int partition,
            @Header(KafkaHeaders.OFFSET) long offset) {

        if (event == null) {
            log.warn(
                    "[Kafka] Message nul ignore : partition={} offset={}",
                    partition,
                    offset
            );
            return;
        }

        log.info(
                "[Kafka] Message recu : eventId={} rendezvousId={} "
                        + "partition={} offset={}",
                event.eventId(),
                event.rendezvousId(),
                partition,
                offset
        );

        if (event.eventVersion() != 1) {
            throw new IllegalArgumentException(
                    "Version evenement non supportee : "
                            + event.eventVersion()
            );
        }

        NotificationEmailDto notification = new NotificationEmailDto(
                event.email(),
                event.sujet(),
                event.contenu()
        );

        var erreurs = validator.validate(notification);

        if (!erreurs.isEmpty()) {
            throw new ConstraintViolationException(
                    "Evenement invalide eventId=" + event.eventId(),
                    erreurs
            );
        }

        notificationService.envoyerEmail(notification);

        log.info(
                "[Kafka] E-mail envoye : eventId={} rendezvousId={} "
                        + "destinataire={}",
                event.eventId(),
                event.rendezvousId(),
                event.email()
        );
    }
}
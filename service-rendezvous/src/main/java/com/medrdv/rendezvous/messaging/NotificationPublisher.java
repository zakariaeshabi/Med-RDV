package com.medrdv.rendezvous.messaging;

import com.medrdv.rendezvous.messaging.event.NotificationEmailRequestedEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.UUID;

@Service
public class NotificationPublisher {

    private static final Logger log =
            LoggerFactory.getLogger(NotificationPublisher.class);

    private final KafkaTemplate<String, NotificationEmailRequestedEvent> kafkaTemplate;
    private final String topic;

    public NotificationPublisher(
            KafkaTemplate<String, NotificationEmailRequestedEvent> kafkaTemplate,
            @Value("${app.kafka.topic.notification}") String topic) {

        this.kafkaTemplate = kafkaTemplate;
        this.topic = topic;
    }

    public void envoyer(
            Long rendezvousId,
            String correlationId,
            String email,
            String sujet,
            String contenu) {

        NotificationEmailRequestedEvent event =
                new NotificationEmailRequestedEvent(
                        UUID.randomUUID(),
                        1,
                        Instant.now().toString(),
                        correlationId,
                        rendezvousId,
                        email,
                        sujet,
                        contenu
                );

        try {
            kafkaTemplate.send(topic, rendezvousId.toString(), event)
                    .whenComplete((result, ex) -> {
                        if (ex != null) {
                            log.error(
                                    "[Kafka] Echec publication : "
                                            + "rendezvousId={} eventId={}",
                                    rendezvousId,
                                    event.eventId(),
                                    ex
                            );
                        } else {
                            log.info(
                                    "[Kafka] Evenement notification publie : "
                                            + "rendezvousId={} partition={} "
                                            + "offset={} eventId={}",
                                    rendezvousId,
                                    result.getRecordMetadata().partition(),
                                    result.getRecordMetadata().offset(),
                                    event.eventId()
                            );
                        }
                    });
        } catch (RuntimeException ex) {
            log.error(
                    "[Kafka] Echec immediat de publication : "
                            + "rendezvousId={} eventId={}",
                    rendezvousId,
                    event.eventId(),
                    ex
            );
        }
    }
}
package com.medrdv.notifications.messaging;

import com.medrdv.notifications.config.RabbitConfig;
import com.medrdv.notifications.dto.NotificationEmailDto;
import com.medrdv.notifications.service.NotificationService;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Validator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
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

    @RabbitListener(queues = RabbitConfig.QUEUE_EMAIL)
    public void recevoir(NotificationEmailDto notification) {
        var erreurs = validator.validate(notification);

        if (!erreurs.isEmpty()) {
            throw new ConstraintViolationException(erreurs);
        }

        notificationService.envoyerEmail(notification);
        log.info("Notification SMTP envoyee avec succes");
    }
}
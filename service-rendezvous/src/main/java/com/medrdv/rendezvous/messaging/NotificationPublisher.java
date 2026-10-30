package com.medrdv.rendezvous.messaging;

import com.medrdv.rendezvous.config.RabbitConfig;
import com.medrdv.rendezvous.dto.NotificationEmailDto;
import org.springframework.amqp.core.MessageDeliveryMode;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

@Service
public class NotificationPublisher {

    private final RabbitTemplate rabbitTemplate;

    public NotificationPublisher(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }

    public void envoyer(NotificationEmailDto notification) {
        rabbitTemplate.convertAndSend(
                "",
                RabbitConfig.QUEUE_EMAIL,
                notification,
                message -> {
                    message.getMessageProperties()
                            .setDeliveryMode(MessageDeliveryMode.PERSISTENT);
                    return message;
                }
        );
    }
}
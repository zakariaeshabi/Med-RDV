package com.medrdv.notifications.service;

import com.medrdv.notifications.dto.NotificationEmailDto;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class NotificationService {

    private final JavaMailSender mailSender;

    public NotificationService(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    public void envoyerEmail(NotificationEmailDto notification) {
        SimpleMailMessage message = new SimpleMailMessage();

        message.setFrom("noreply@medrdv.local");
        message.setTo(notification.destinataire());
        message.setSubject(notification.objet());
        message.setText(notification.contenu());

        mailSender.send(message);
    }
}
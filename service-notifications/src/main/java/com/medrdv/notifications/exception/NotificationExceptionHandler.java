package com.medrdv.notifications.exception;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.MailException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class NotificationExceptionHandler {

    private static final Logger log =
            LoggerFactory.getLogger(NotificationExceptionHandler.class);

    @ExceptionHandler(MailException.class)
    public ResponseEntity<String> erreurSmtp(MailException ex) {
        log.error("Echec de l'envoi SMTP vers Mailpit", ex);

        return ResponseEntity.status(503)
                .body("Impossible de transmettre l'email au serveur SMTP");
    }
}
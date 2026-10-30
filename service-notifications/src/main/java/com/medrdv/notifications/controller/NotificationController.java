package com.medrdv.notifications.controller;

import com.medrdv.notifications.dto.NotificationEmailDto;
import com.medrdv.notifications.service.NotificationService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/notifications")
public class NotificationController {

    private final NotificationService notificationService;

    public NotificationController(
            NotificationService notificationService) {
        this.notificationService = notificationService;
    }

    @PostMapping("/email")
    public ResponseEntity<Void> envoyer(
            @Valid @RequestBody NotificationEmailDto notification) {

        notificationService.envoyerEmail(notification);

        return ResponseEntity.noContent().build();
    }
}
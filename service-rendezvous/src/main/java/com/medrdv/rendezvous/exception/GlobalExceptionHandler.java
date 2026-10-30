package com.medrdv.rendezvous.exception;

import feign.FeignException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<String> refus(IllegalArgumentException ex) {
        return ResponseEntity.badRequest().body(ex.getMessage());
    }

    @ExceptionHandler(FeignException.NotFound.class)
    public ResponseEntity<String> externeIntrouvable(
            FeignException.NotFound ex) {
        return ResponseEntity.status(404)
                .body("Patient ou praticien introuvable");
    }

    @ExceptionHandler(FeignException.class)
    public ResponseEntity<String> erreurFeign(FeignException ex) {
        if (ex.status() == -1 || ex.status() == 503) {
            return ResponseEntity.status(503)
                    .body("Service patients ou praticiens indisponible");
        }

        return ResponseEntity.status(502)
                .body("Erreur HTTP du service distant : " + ex.status());
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<String> conflitBase(
            DataIntegrityViolationException ex) {
        return ResponseEntity.status(409)
                .body("Conflit d'enregistrement : verifier la cle "
                        + "d'idempotence et les creneaux");
    }
}
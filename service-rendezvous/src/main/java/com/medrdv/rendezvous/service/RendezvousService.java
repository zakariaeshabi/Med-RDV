package com.medrdv.rendezvous.service;

import com.medrdv.rendezvous.dto.NotificationEmailDto;
import com.medrdv.rendezvous.messaging.NotificationPublisher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.AmqpException;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import com.medrdv.rendezvous.dto.CreateRendezvousRequest;
import com.medrdv.rendezvous.dto.RendezvousDetailDto;
import com.medrdv.rendezvous.dto.RendezvousDto;
import com.medrdv.rendezvous.entity.Rendezvous;
import com.medrdv.rendezvous.feign.PatientDto;
import com.medrdv.rendezvous.feign.ResilientRequests;
import com.medrdv.rendezvous.feign.PraticienDto;
import com.medrdv.rendezvous.repository.RendezvousRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;

@Service
public class RendezvousService {

    private final RendezvousRepository repository;
    private final ResilientRequests resilientRequests;

    private static final Logger log =
            LoggerFactory.getLogger(RendezvousService.class);

    private final NotificationPublisher notificationPublisher;

    public RendezvousService(
            RendezvousRepository repository,
            ResilientRequests resilientRequests,
            NotificationPublisher notificationPublisher) {

        this.repository = repository;
        this.resilientRequests = resilientRequests;
        this.notificationPublisher = notificationPublisher;
    }

    @Transactional
    public RendezvousDto creer(
            CreateRendezvousRequest request,
            String idempotencyKey) {

        if (idempotencyKey == null
                || idempotencyKey.isBlank()
                || idempotencyKey.length() > 255) {
            throw new IllegalArgumentException(
                    "Cle d'idempotence invalide"
            );
        }

        if (repository.existsByIdempotencyKey(idempotencyKey)) {
            throw new IllegalArgumentException(
                    "Operation deja traitee"
            );
        }

        LocalDateTime date = request.dateHeure();

        if (!date.isAfter(LocalDateTime.now())) {
            throw new IllegalArgumentException(
                    "La date du rendez-vous doit etre future"
            );
        }

        if (date.getMinute() % 30 != 0
                || date.getSecond() != 0
                || date.getNano() != 0) {
            throw new IllegalArgumentException(
                    "Le creneau doit commencer a :00 ou :30"
            );
        }

        PatientDto patient = resilientRequests.patientStrict(request.patientId());

        if (!patient.actif()) {
            throw new IllegalArgumentException("Patient inactif");
        }

        PraticienDto praticien =
                resilientRequests.praticienStrict(request.praticienId());

        if (!praticien.actif()) {
            throw new IllegalArgumentException("Praticien inactif");
        }

        if (repository.existsByPraticienIdAndDateHeure(
                request.praticienId(), date)) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "Ce praticien a deja un rendez-vous sur ce creneau"
            );
        }

        if (repository.existsByPatientIdAndDateHeure(
                request.patientId(), date)) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "Ce patient a deja un rendez-vous sur ce creneau"
            );
        }

        Rendezvous rendezvous = new Rendezvous(
                request.patientId(),
                request.praticienId(),
                date,
                idempotencyKey
        );

        // JALON5_NOTIFICATION_AFTER_COMMIT
        Rendezvous saved = repository.saveAndFlush(rendezvous);

        NotificationEmailDto notification = new NotificationEmailDto(
                patient.email(),
                "Med-RDV - Rendez-vous n° " + saved.getId() + " confirme",
                "Bonjour " + patient.prenom() + " " + patient.nom()
                        + ", votre rendez-vous n° " + saved.getId()
                        + " avec " + praticien.prenom() + " "
                        + praticien.nom()
                        + " est confirme pour le "
                        + saved.getDateHeure() + "."
        );

        publierApresCommit(notification);
        return toDto(saved);
    }

    @Transactional(readOnly = true)
    public RendezvousDto obtenir(Long id) {
        return toDto(charger(id));
    }

    @io.github.resilience4j.ratelimiter.annotation.RateLimiter(
            name = "consultation",
            fallbackMethod = "consultationLimitee")
    @Transactional(readOnly = true)
    public RendezvousDetailDto obtenirDetail(Long id) {
        Rendezvous rendezvous = charger(id);

        PatientDto patient =
                resilientRequests.patientLecture(rendezvous.getPatientId());

        PraticienDto praticien =
                resilientRequests.praticienLecture(rendezvous.getPraticienId());

        return new RendezvousDetailDto(
                toDto(rendezvous),
                patient,
                praticien
        );
    }

    private void publierApresCommit(NotificationEmailDto notification) {
        TransactionSynchronizationManager.registerSynchronization(
                new TransactionSynchronization() {
                    @Override
                    public void afterCommit() {
                        try {
                            notificationPublisher.envoyer(notification);
                            log.info("Demande de notification publiee");
                        } catch (AmqpException ex) {
                            log.error(
                                    "Rendez-vous enregistre, mais publication "
                                            + "de la notification en echec",
                                    ex
                            );
                        }
                    }
                }
        );
    }

    private Rendezvous charger(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Rendez-vous introuvable"
                ));
    }

    private RendezvousDto toDto(Rendezvous rendezvous) {
        return new RendezvousDto(
                rendezvous.getId(),
                rendezvous.getPatientId(),
                rendezvous.getPraticienId(),
                rendezvous.getDateHeure(),
                rendezvous.getStatut()
        );
    }
    public RendezvousDetailDto consultationLimitee(
            Long id,
            io.github.resilience4j.ratelimiter.RequestNotPermitted cause) {
        log.warn("Quota consultation atteint : donnees locales seules");
        return new RendezvousDetailDto(toDto(charger(id)), null, null);
    }

}
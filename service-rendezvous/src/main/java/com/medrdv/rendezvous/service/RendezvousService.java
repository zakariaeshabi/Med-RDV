package com.medrdv.rendezvous.service;

import com.medrdv.rendezvous.dto.CreateRendezvousRequest;
import com.medrdv.rendezvous.dto.RendezvousDetailDto;
import com.medrdv.rendezvous.dto.RendezvousDto;
import com.medrdv.rendezvous.entity.Rendezvous;
import com.medrdv.rendezvous.feign.PatientDto;
import com.medrdv.rendezvous.feign.PatientsClient;
import com.medrdv.rendezvous.feign.PraticienDto;
import com.medrdv.rendezvous.feign.PraticiensClient;
import com.medrdv.rendezvous.repository.RendezvousRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;

@Service
public class RendezvousService {

    private final RendezvousRepository repository;
    private final PatientsClient patientsClient;
    private final PraticiensClient praticiensClient;

    public RendezvousService(
            RendezvousRepository repository,
            PatientsClient patientsClient,
            PraticiensClient praticiensClient) {

        this.repository = repository;
        this.patientsClient = patientsClient;
        this.praticiensClient = praticiensClient;
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

        PatientDto patient = patientsClient.obtenir(request.patientId());

        if (!patient.actif()) {
            throw new IllegalArgumentException("Patient inactif");
        }

        PraticienDto praticien =
                praticiensClient.obtenir(request.praticienId());

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

        return toDto(repository.saveAndFlush(rendezvous));
    }

    @Transactional(readOnly = true)
    public RendezvousDto obtenir(Long id) {
        return toDto(charger(id));
    }

    @Transactional(readOnly = true)
    public RendezvousDetailDto obtenirDetail(Long id) {
        Rendezvous rendezvous = charger(id);

        PatientDto patient =
                patientsClient.obtenir(rendezvous.getPatientId());

        PraticienDto praticien =
                praticiensClient.obtenir(rendezvous.getPraticienId());

        return new RendezvousDetailDto(
                toDto(rendezvous),
                patient,
                praticien
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
}
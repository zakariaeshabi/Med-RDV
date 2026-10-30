package com.medrdv.rendezvous.repository;

import com.medrdv.rendezvous.entity.Rendezvous;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;

public interface RendezvousRepository
        extends JpaRepository<Rendezvous, Long> {

    boolean existsByIdempotencyKey(String idempotencyKey);

    boolean existsByPraticienIdAndDateHeure(
            Long praticienId,
            LocalDateTime dateHeure
    );

    boolean existsByPatientIdAndDateHeure(
            Long patientId,
            LocalDateTime dateHeure
    );
}
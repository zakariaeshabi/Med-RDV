package com.medrdv.patients.controller;

import com.medrdv.patients.dto.CreatePatientRequest;
import com.medrdv.patients.dto.PatientDto;
import com.medrdv.patients.entity.Patient;
import com.medrdv.patients.repository.PatientRepository;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/patients")
public class PatientController {

    private final PatientRepository patientRepository;

    public PatientController(PatientRepository patientRepository) {
        this.patientRepository = patientRepository;
    }

    @PostMapping
    public ResponseEntity<PatientDto> creer(
            @Valid @RequestBody CreatePatientRequest request) {

        Patient patient = new Patient(
                request.nom().trim(),
                request.prenom().trim(),
                request.email().trim(),
                request.actif()
        );

        Patient saved = patientRepository.save(patient);

        return ResponseEntity
                .created(URI.create("/api/patients/" + saved.getId()))
                .body(toDto(saved));
    }

    @GetMapping("/{id}")
    public PatientDto obtenir(@PathVariable("id") Long id) {
        Patient patient = patientRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Patient introuvable"
                ));

        return toDto(patient);
    }

    @GetMapping
    public List<PatientDto> lister() {
        return patientRepository.findAll()
                .stream()
                .map(this::toDto)
                .toList();
    }

    private PatientDto toDto(Patient patient) {
        return new PatientDto(
                patient.getId(),
                patient.getNom(),
                patient.getPrenom(),
                patient.getEmail(),
                patient.isActif()
        );
    }
}
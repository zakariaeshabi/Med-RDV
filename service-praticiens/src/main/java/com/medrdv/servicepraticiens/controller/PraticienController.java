package com.medrdv.servicepraticiens.controller;

import com.medrdv.servicepraticiens.dto.CreatePraticienRequest;
import com.medrdv.servicepraticiens.dto.PraticienDto;
import com.medrdv.servicepraticiens.entity.Praticien;
import com.medrdv.servicepraticiens.repository.PraticienRepository;
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
@RequestMapping("/api/praticiens")
public class PraticienController {

    private final PraticienRepository praticienRepository;

    public PraticienController(
            PraticienRepository praticienRepository) {
        this.praticienRepository = praticienRepository;
    }

    @PostMapping
    public ResponseEntity<PraticienDto> creer(
            @Valid @RequestBody CreatePraticienRequest request) {

        Praticien praticien = new Praticien(
                request.nom().trim(),
                request.prenom().trim(),
                request.specialite().trim(),
                request.actif()
        );

        Praticien saved = praticienRepository.save(praticien);

        return ResponseEntity
                .created(URI.create("/api/praticiens/" + saved.getId()))
                .body(toDto(saved));
    }

    @GetMapping("/{id}")
    public PraticienDto obtenir(@PathVariable("id") Long id) {
        Praticien praticien = praticienRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Praticien introuvable"
                ));

        return toDto(praticien);
    }

    @GetMapping
    public List<PraticienDto> lister() {
        return praticienRepository.findAll()
                .stream()
                .map(this::toDto)
                .toList();
    }

    private PraticienDto toDto(Praticien praticien) {
        return new PraticienDto(
                praticien.getId(),
                praticien.getNom(),
                praticien.getPrenom(),
                praticien.getSpecialite(),
                praticien.isActif()
        );
    }
}
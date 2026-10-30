package com.medrdv.rendezvous.controller;

import com.medrdv.rendezvous.dto.CreateRendezvousRequest;
import com.medrdv.rendezvous.dto.RendezvousDetailDto;
import com.medrdv.rendezvous.dto.RendezvousDto;
import com.medrdv.rendezvous.service.RendezvousService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;

@RestController
@RequestMapping("/api/rendezvous")
public class RendezvousController {

    private final RendezvousService service;

    public RendezvousController(RendezvousService service) {
        this.service = service;
    }

    @PostMapping
    public ResponseEntity<RendezvousDto> creer(
            @Valid @RequestBody CreateRendezvousRequest request,
            @RequestHeader("idempotencyKey") String idempotencyKey) {

        RendezvousDto saved = service.creer(request, idempotencyKey);

        return ResponseEntity
                .created(URI.create("/api/rendezvous/" + saved.id()))
                .body(saved);
    }

    @GetMapping("/{id}")
    public RendezvousDto obtenir(@PathVariable("id") Long id) {
        return service.obtenir(id);
    }

    @GetMapping("/{id}/detail")
    public RendezvousDetailDto obtenirDetail(
            @PathVariable("id") Long id) {
        return service.obtenirDetail(id);
    }
}
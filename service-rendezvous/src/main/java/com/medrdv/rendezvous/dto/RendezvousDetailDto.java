package com.medrdv.rendezvous.dto;

import com.medrdv.rendezvous.feign.PatientDto;
import com.medrdv.rendezvous.feign.PraticienDto;

public record RendezvousDetailDto(
        RendezvousDto rendezvous,
        PatientDto patient,
        PraticienDto praticien
) {
}
package com.medrdv.rendezvous.feign;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "service-patients")
public interface PatientsClient {

    @GetMapping("/api/patients/{id}")
    PatientDto obtenir(@PathVariable("id") Long id);
}
package com.medrdv.rendezvous.feign;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "service-praticiens")
public interface PraticiensClient {

    @GetMapping("/api/praticiens/{id}")
    PraticienDto obtenir(@PathVariable("id") Long id);
}
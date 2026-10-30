package com.medrdv.servicepraticiens.repository;

import com.medrdv.servicepraticiens.entity.Praticien;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PraticienRepository
        extends JpaRepository<Praticien, Long> {
}
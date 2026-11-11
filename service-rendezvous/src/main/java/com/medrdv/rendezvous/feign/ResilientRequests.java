package com.medrdv.rendezvous.feign;

import com.medrdv.rendezvous.exception.ServiceExterneIndisponibleException;
import feign.FeignException;
import io.github.resilience4j.bulkhead.annotation.Bulkhead;
import io.github.resilience4j.circuitbreaker.CallNotPermittedException;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class ResilientRequests {

    private static final Logger log =
            LoggerFactory.getLogger(ResilientRequests.class);

    private final PatientsClient patientsClient;
    private final PraticiensClient praticiensClient;

    public ResilientRequests(
            PatientsClient patientsClient,
            PraticiensClient praticiensClient) {
        this.patientsClient = patientsClient;
        this.praticiensClient = praticiensClient;
    }

    @Retry(name = "service-patients", fallbackMethod = "patientIndisponible")
    @CircuitBreaker(name = "service-patients")
    @Bulkhead(name = "service-patients")
    public PatientDto patientStrict(Long id) {
        log.info("Tentative Feign patientStrict : patient {}", id);
        return patientsClient.obtenir(id);
    }

    @Retry(name = "service-praticiens",
            fallbackMethod = "praticienIndisponible")
    @CircuitBreaker(name = "service-praticiens")
    @Bulkhead(name = "service-praticiens")
    public PraticienDto praticienStrict(Long id) {
        log.info("Tentative Feign praticienStrict : praticien {}", id);
        return praticiensClient.obtenir(id);
    }

    @Retry(name = "service-patients", fallbackMethod = "patientDeRepli")
    @CircuitBreaker(name = "service-patients")
    @Bulkhead(name = "service-patients")
    public PatientDto patientLecture(Long id) {
        log.info("Tentative Feign patientLecture : patient {}", id);
        return patientsClient.obtenir(id);
    }

    @Retry(name = "service-praticiens",
            fallbackMethod = "praticienDeRepli")
    @CircuitBreaker(name = "service-praticiens")
    @Bulkhead(name = "service-praticiens")
    public PraticienDto praticienLecture(Long id) {
        log.info("Tentative Feign praticienLecture : praticien {}", id);
        return praticiensClient.obtenir(id);
    }

    private PatientDto patientIndisponible(Long id, RuntimeException ex) {
        if (!estIndisponible(ex)) {
            throw ex;
        }
        log.warn("Creation interrompue : patients indisponible ({})",
                ex.getClass().getSimpleName());
        throw new ServiceExterneIndisponibleException(
                "Service patients indisponible : creation impossible", ex);
    }

    private PraticienDto praticienIndisponible(
            Long id, RuntimeException ex) {
        if (!estIndisponible(ex)) {
            throw ex;
        }
        log.warn("Creation interrompue : praticiens indisponible ({})",
                ex.getClass().getSimpleName());
        throw new ServiceExterneIndisponibleException(
                "Service praticiens indisponible : creation impossible", ex);
    }

    private PatientDto patientDeRepli(Long id, RuntimeException ex) {
        if (!estIndisponible(ex)) {
            throw ex;
        }
        log.warn("Consultation degradee : patient {} indisponible ({})",
                id, ex.getClass().getSimpleName());
        return null;
    }

    private PraticienDto praticienDeRepli(Long id, RuntimeException ex) {
        if (!estIndisponible(ex)) {
            throw ex;
        }
        log.warn("Consultation degradee : praticien {} indisponible ({})",
                id, ex.getClass().getSimpleName());
        return null;
    }

    private boolean estIndisponible(RuntimeException ex) {
        return ex instanceof CallNotPermittedException
                || (ex instanceof FeignException feign
                && (feign.status() == -1 || feign.status() == 503));
    }
}

package com.medrdv.rendezvous.exception;

public class ServiceExterneIndisponibleException extends RuntimeException {

    public ServiceExterneIndisponibleException(
            String message, Throwable cause) {
        super(message, cause);
    }
}

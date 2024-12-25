package com.smp.mail.exception;

public class ServiceRetrievalException extends RuntimeException {
    public ServiceRetrievalException(String message) {
        super(message);
    }

    public ServiceRetrievalException(String message, Throwable cause) {
        super(message, cause);
    }
}
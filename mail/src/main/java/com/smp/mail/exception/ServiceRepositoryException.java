package com.smp.mail.exception;

public class ServiceRepositoryException extends RuntimeException {
    public ServiceRepositoryException(String message, Throwable cause) {
        super(message, cause);
    }
}
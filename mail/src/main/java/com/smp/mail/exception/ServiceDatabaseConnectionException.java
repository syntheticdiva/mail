package com.smp.mail.exception;

public class ServiceDatabaseConnectionException extends RuntimeException {
    public ServiceDatabaseConnectionException(String message, Throwable cause) {
        super(message, cause);
    }
}

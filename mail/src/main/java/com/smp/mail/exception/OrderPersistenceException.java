package com.smp.mail.exception;

public class OrderPersistenceException extends OrderException {
    public OrderPersistenceException(String message) {
        super(message);
    }

    public OrderPersistenceException(String message, Throwable cause) {
        super(message, cause);
    }
}
package com.smp.mail.exception;

public class OrderCreationException extends OrderException {
    public OrderCreationException(String message) {
        super(message);
    }

    public OrderCreationException(String message, Throwable cause) {
        super(message, cause);
    }
}
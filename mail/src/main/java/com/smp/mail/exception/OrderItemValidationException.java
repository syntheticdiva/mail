package com.smp.mail.exception;

public class OrderItemValidationException extends OrderException {
    public OrderItemValidationException(String message) {
        super(message);
    }

    public OrderItemValidationException(String message, Throwable cause) {
        super(message, cause);
    }
}
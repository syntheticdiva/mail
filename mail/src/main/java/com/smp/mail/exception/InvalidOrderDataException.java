package com.smp.mail.exception;

public class InvalidOrderDataException extends OrderException {
    public InvalidOrderDataException(String message) {
        super(message);
    }

    public InvalidOrderDataException(String message, Throwable cause) {
        super(message, cause);
    }
}


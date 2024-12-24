package com.smp.mail.exception;

import lombok.Data;

@Data
public class ErrorResponse {
    private String message;
    private String cause;
    private String resolution;
}


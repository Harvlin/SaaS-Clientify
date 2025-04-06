package com.project.SaasCRM.exception;

public class DealNotFoundException extends RuntimeException {
    public DealNotFoundException(String message) {
        super(message);
    }

    public DealNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
} 
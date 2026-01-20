package com.moneymanger.moneytracker.GlobalExceptionHandler;

import org.springframework.http.HttpStatus;

public class ResourceNotFoundException extends RuntimeException {

    private final HttpStatus status;

    public ResourceNotFoundException(String message, HttpStatus status) {
        super(message); // ✅ Pass message to RuntimeException
        this.status = status;
    }

    public HttpStatus getStatus() {
        return status;
    }
}

package com.moneymanger.moneytracker.GlobalExceptionHandler;

import java.time.LocalDateTime;
import org.springframework.http.HttpStatus;

public class ErrorResponse {
    private LocalDateTime timestamp;
    private String message;
    private HttpStatus status;
    private String path;

    public ErrorResponse(String message, HttpStatus status, String path) {
        this.timestamp = LocalDateTime.now();
        this.message = message;
        this.status = status;
        this.path = path;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public String getMessage() {
        return message;
    }

    public HttpStatus getStatus() {
        return status;
    }

    public String getPath() {
        return path;
    }
}

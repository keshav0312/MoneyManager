package com.moneymanger.moneytracker.GlobalExceptionHandler;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleResourceNotFound(ResourceNotFoundException ex, WebRequest request) {
        ErrorResponse error = new ErrorResponse(
                ex.getMessage(),
                ex.getStatus(),
                request.getDescription(false).replace("uri=", "")
        );
        return new ResponseEntity<>(error, ex.getStatus());
    }

    // Optional: Catch all other exceptions
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleAllExceptions(Exception ex, WebRequest request) {
        ErrorResponse error = new ErrorResponse(
                ex.getMessage(),
                org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR,
                request.getDescription(false).replace("uri=", "")
        );
        return new ResponseEntity<>(error, org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR);
    }
}

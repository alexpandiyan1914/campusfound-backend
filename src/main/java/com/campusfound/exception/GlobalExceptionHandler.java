package com.campusfound.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<Map<String, String>> handleRuntimeException(
            RuntimeException exception) {

        String message = exception.getMessage();

        HttpStatus status = HttpStatus.BAD_REQUEST;

        if (message != null &&
                message.equals("You have already submitted a claim for this item")) {

            status = HttpStatus.CONFLICT;
        }

        return ResponseEntity
                .status(status)
                .body(Map.of(
                        "message",
                        message != null
                                ? message
                                : "Something went wrong"
                ));
    }
}
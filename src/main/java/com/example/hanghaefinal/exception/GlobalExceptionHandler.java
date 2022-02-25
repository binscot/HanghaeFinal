package com.example.hanghaefinal.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.io.IOException;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(value = { IllegalArgumentException.class })
    public ResponseEntity<Object> handleApiRequestException(IllegalArgumentException ex) {
        RestApiException restApiException = new RestApiException();
        restApiException.setHttpStatus(HttpStatus.BAD_REQUEST);
        restApiException.setErrorMessage(ex.getMessage());

        return new ResponseEntity<>(
                restApiException,
                HttpStatus.BAD_REQUEST
        );
    }

    @ExceptionHandler(value = { NullPointerException.class })
    public ResponseEntity<Object> handleApiRequestException(NullPointerException ex) {
        RestApiException restApiException = new RestApiException();
        restApiException.setHttpStatus(HttpStatus.BAD_REQUEST);
        restApiException.setErrorMessage(ex.getMessage());

        return new ResponseEntity<>(
                restApiException,
                HttpStatus.BAD_REQUEST
        );
    }

    @ExceptionHandler(value = { IOException.class })
    public ResponseEntity<Object> handleApiRequestException(IOException ex) {
        RestApiException restApiException = new RestApiException();
        restApiException.setHttpStatus(HttpStatus.BAD_REQUEST);
        restApiException.setErrorMessage(ex.getMessage());

        return new ResponseEntity<>(
                restApiException,
                HttpStatus.BAD_REQUEST
        );
    }
}


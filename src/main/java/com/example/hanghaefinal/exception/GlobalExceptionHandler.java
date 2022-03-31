package com.example.hanghaefinal.exception;

import com.example.hanghaefinal.exception.exception.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
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

    @ExceptionHandler(UsernameNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleUsernameNotFoundException(UsernameNotFoundException e) {
        return new ResponseEntity<>(new ErrorResponse("U001", e.getMessage()), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(AdminOnlyException.class)
    public ResponseEntity<ErrorResponse> handleAdminOnlyException(AdminOnlyException e){
        return new ResponseEntity<>(new ErrorResponse("", e.getMessage()), HttpStatus.UNAUTHORIZED);

    }

    @ExceptionHandler(CommentLimitException.class)
    public ResponseEntity<ErrorResponse> handleCommentLimitException(CommentLimitException e){
        return new ResponseEntity<>(new ErrorResponse("", e.getMessage()), HttpStatus.UNAUTHORIZED);

    }
    @ExceptionHandler(CommentNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleCommentNotFoundException(CommentNotFoundException e){
        return new ResponseEntity<>(new ErrorResponse("", e.getMessage()), HttpStatus.UNAUTHORIZED);

    }
    @ExceptionHandler(ContentNullException.class)
    public ResponseEntity<ErrorResponse> handleContentNullException(ContentNullException e){
        return new ResponseEntity<>(new ErrorResponse("", e.getMessage()), HttpStatus.UNAUTHORIZED);

    }
    @ExceptionHandler(EqualPasswordException.class)
    public ResponseEntity<ErrorResponse> handleEqualPasswordException(EqualPasswordException e){
        return new ResponseEntity<>(new ErrorResponse("", e.getMessage()), HttpStatus.UNAUTHORIZED);

    }
    @ExceptionHandler(AdminOnlyException.class)
    public ResponseEntity<ErrorResponse> handleAdminOnlyException(AdminOnlyException e){
        return new ResponseEntity<>(new ErrorResponse("", e.getMessage()), HttpStatus.UNAUTHORIZED);

    }
    @ExceptionHandler(AdminOnlyException.class)
    public ResponseEntity<ErrorResponse> handleAdminOnlyException(AdminOnlyException e){
        return new ResponseEntity<>(new ErrorResponse("", e.getMessage()), HttpStatus.UNAUTHORIZED);

    }
    @ExceptionHandler(AdminOnlyException.class)
    public ResponseEntity<ErrorResponse> handleAdminOnlyException(AdminOnlyException e){
        return new ResponseEntity<>(new ErrorResponse("", e.getMessage()), HttpStatus.UNAUTHORIZED);

    }    @ExceptionHandler(AdminOnlyException.class)
    public ResponseEntity<ErrorResponse> handleAdminOnlyException(AdminOnlyException e){
        return new ResponseEntity<>(new ErrorResponse("", e.getMessage()), HttpStatus.UNAUTHORIZED);

    }



}


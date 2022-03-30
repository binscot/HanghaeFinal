package com.example.hanghaefinal.exception.exception;

public class AdminOnlyException extends RuntimeException{
    public AdminOnlyException(String message) {
        super(message);
    }
}
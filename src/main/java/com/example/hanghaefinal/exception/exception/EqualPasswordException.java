package com.example.hanghaefinal.exception.exception;

public class EqualPasswordException extends RuntimeException{
    public EqualPasswordException(String message) {
        super(message);
    }
}
package ru.itmo.exception;

public class ValidateException extends RuntimeException {

    public ValidateException(String message) {
        super(message);
    }
}
package ru.itmo.exception;

public class ServerException extends RuntimeException {
    public ServerException(String message) {
        super("Server error: " + message);
    }
}

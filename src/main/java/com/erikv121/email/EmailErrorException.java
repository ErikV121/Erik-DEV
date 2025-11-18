package com.erikv121.email;

public class EmailErrorException extends RuntimeException {
    public EmailErrorException(String message) {
        super(message);
    }
}

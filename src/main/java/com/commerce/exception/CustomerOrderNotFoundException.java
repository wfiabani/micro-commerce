package com.commerce.exception;

public class CustomerOrderNotFoundException extends RuntimeException {

    public CustomerOrderNotFoundException(String message) {
        super(message);
    }

}

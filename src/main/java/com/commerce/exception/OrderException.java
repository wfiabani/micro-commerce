package com.commerce.exception;

public class OrderException extends RuntimeException{
    public OrderException(String message) {
        super(message);
    }
}
package com.ramon.exception;

public class OrderNotFoundException extends RuntimeException {

    public OrderNotFoundException(Long id) {
      super("Could not find employee " + id);
    }
}
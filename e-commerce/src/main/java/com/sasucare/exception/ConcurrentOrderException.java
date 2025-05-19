package com.sasucare.exception;

/**
 * Exception thrown when concurrent orders conflict with each other.
 * This happens when multiple users try to order the same product with limited stock simultaneously.
 */
public class ConcurrentOrderException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    public ConcurrentOrderException(String message) {
        super(message);
    }

    public ConcurrentOrderException(String message, Throwable cause) {
        super(message, cause);
    }
}

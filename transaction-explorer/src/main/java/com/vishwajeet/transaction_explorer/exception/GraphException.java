package com.vishwajeet.transaction_explorer.exception;

/**
 * Base exception for all graph-related business errors.
 */
public class GraphException extends RuntimeException {
    public GraphException(String message) {
        super(message);
    }
}
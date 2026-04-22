package com.vishwajeet.transaction_explorer.exception;

/**
 * Thrown when a requested Node ID is not found in the repository.
 * Triggers a 404 NOT FOUND via GlobalExceptionHandler.
 */
public class NodeNotFoundException extends RuntimeException {
    public NodeNotFoundException(String id) {
        super("Node with ID '" + id + "' was not found in the system.");
    }
}
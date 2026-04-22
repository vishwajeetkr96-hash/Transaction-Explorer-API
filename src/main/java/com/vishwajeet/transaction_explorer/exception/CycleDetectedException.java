package com.vishwajeet.transaction_explorer.exception;

/**
 * Thrown when a circular dependency is detected during graph traversal.
 * Triggers a 400 BAD REQUEST via GlobalExceptionHandler.
 */
public class CycleDetectedException extends RuntimeException {
    public CycleDetectedException(String id) {
        super("Circular dependency detected at Node ID: " + id + ". Traversal halted to prevent infinite loop.");
    }
}
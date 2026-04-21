package com.vishwajeet.transaction_explorer.exception;

public class CycleDetectedException extends GraphException {
    public CycleDetectedException(String id) {
        super("Circular dependency detected starting at node: " + id);
    }
}
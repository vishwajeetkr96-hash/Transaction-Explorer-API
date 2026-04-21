package com.vishwajeet.transaction_explorer.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.Map;

/**
 * Global interceptor that ensures all errors return a structured JSON
 * response instead of a raw stack trace.
 */
@ControllerAdvice
public class GlobalExceptionHandler {

    // Requirement 4.2: Handle missing nodes with 404
    @ExceptionHandler(NodeNotFoundException.class)
    public ResponseEntity<Map<String, String>> handleNodeNotFound(NodeNotFoundException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of(
                "error", "NODE_NOT_FOUND",
                "message", ex.getMessage()
        ));
    }

    // Requirement 5.3: Handle graph cycles with 400
    @ExceptionHandler(CycleDetectedException.class)
    public ResponseEntity<Map<String, String>> handleCycleDetected(CycleDetectedException ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of(
                "error", "CYCLE_DETECTED",
                "message", ex.getMessage()
        ));
    }

    // Handle invalid query parameters (like maxDepth < 0)
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<Map<String, String>> handleInvalidInput(IllegalArgumentException ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of(
                "error", "INVALID_INPUT",
                "message", ex.getMessage()
        ));
    }

    // Generic fallback for any other unexpected errors
    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, String>> handleGeneralError(Exception ex) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of(
                "error", "INTERNAL_SERVER_ERROR",
                "message", "An unexpected system error occurred."
        ));
    }
}
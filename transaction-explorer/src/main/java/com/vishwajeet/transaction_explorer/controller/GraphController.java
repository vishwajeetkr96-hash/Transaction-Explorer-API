package com.vishwajeet.transaction_explorer.controller;

import com.vishwajeet.transaction_explorer.exception.CycleDetectedException;
import com.vishwajeet.transaction_explorer.exception.NodeNotFoundException;
import com.vishwajeet.transaction_explorer.model.NodeResponse;
import com.vishwajeet.transaction_explorer.model.NodeTransaction;
import com.vishwajeet.transaction_explorer.service.GraphService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * REST Controller for the Transaction Graph Node Explorer.
 * Orchestrates requirements for node hierarchy exploration,
 * level computation, and transaction aggregation.
 */
@RestController
@RequestMapping("/api/graph")
public class GraphController {

    private final GraphService graphService;

    @Autowired
    public GraphController(GraphService graphService) {
        this.graphService = graphService;
    }

    /**
     * Requirement 4.2: Primary Node Explorer.
     * Computes Node Level, builds the Parent Chain (Root -> Parent),
     * identifies Direct Children, and aggregates Next-Level Transactions.
     * * Requirement 5.1 & 5.3: Includes MaxDepth support and Cycle Detection.
     * * @param id The Node ID to explore (e.g., N1, N2).
     * @param maxDepth Optional limit for child tree traversal (default=1, max=5).
     * @throws IllegalArgumentException if maxDepth is < 0 or > 5.
     * @throws NodeNotFoundException if node does not exist (404).
     * @throws CycleDetectedException if a graph cycle is discovered (400).
     */
    @GetMapping("/nodes/{id}")
    public ResponseEntity<NodeResponse> getNodeDetails(
            @PathVariable String id,
            @RequestParam(defaultValue = "1") int maxDepth) {

        // Bonus 5.1: Constraint Validation
        if (maxDepth < 0 || maxDepth > 5) {
            throw new IllegalArgumentException("maxDepth must be between 0 and 5.");
        }

        // The Service handles Requirement 4.2 (isRoot, isLeaf, Level, ParentChain)
        NodeResponse response = graphService.exploreNode(id, maxDepth);
        return ResponseEntity.ok(response);
    }

    /**
     * Requirement 5.4: Specialized Transaction Filtering.
     * Provides filtered access to child-node transactions based on amount and type.
     * * @param id The Parent Node ID.
     * @param minAmount Minimum transaction amount filter.
     * @param maxAmount Maximum transaction amount filter.
     * @param txnType Filter by type (e.g., POS, ATM, SALARY).
     */
    @GetMapping("/nodes/{id}/children-transactions")
    public ResponseEntity<List<NodeTransaction>> getFilteredTransactions(
            @PathVariable String id,
            @RequestParam(required = false) Double minAmount,
            @RequestParam(required = false) Double maxAmount,
            @RequestParam(required = false) String txnType) {

        // Validate range if both are provided
        if (minAmount != null && maxAmount != null && minAmount > maxAmount) {
            throw new IllegalArgumentException("minAmount cannot be greater than maxAmount.");
        }

        List<NodeTransaction> filteredResults = graphService.getFilteredChildTransactions(
                id, minAmount, maxAmount, txnType);

        return ResponseEntity.ok(filteredResults);
    }
}
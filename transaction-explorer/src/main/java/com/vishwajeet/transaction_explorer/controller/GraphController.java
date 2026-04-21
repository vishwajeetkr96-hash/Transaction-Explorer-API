package com.vishwajeet.transaction_explorer.controller;

import com.vishwajeet.transaction_explorer.model.NodeResponse;
import com.vishwajeet.transaction_explorer.model.NodeTransaction;
import com.vishwajeet.transaction_explorer.service.GraphService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * REST Controller for the Transaction Graph Node Explorer.
 * Exposes endpoints for hierarchy exploration and transaction filtering.
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
     * Requirement 4.2: Main Node Explorer Endpoint.
     * Requirement 5.1: Added maxDepth support (Bonus).
     * * @param id The Node ID to explore (e.g., N1, N2).
     * @param maxDepth Level limit for child tree traversal (default=1, max=5).
     * @return Full node details including level, parent chain, and direct children.
     */
    @GetMapping("/nodes/{id}")
    public ResponseEntity<NodeResponse> getNodeDetails(
            @PathVariable String id,
            @RequestParam(defaultValue = "1") int maxDepth) {

        // Validate depth constraint before calling service (Bonus 5.1)
        if (maxDepth < 0 || maxDepth > 5) {
            throw new IllegalArgumentException("maxDepth must be between 0 and 5.");
        }

        // Service throws NodeNotFoundException or CycleDetectedException
        // which are caught by our GlobalExceptionHandler.
        NodeResponse response = graphService.exploreNode(id, maxDepth);
        return ResponseEntity.ok(response);
    }

    /**
     * Requirement 5.4: Filtered Children Transactions (Optional).
     * Returns only transactions from child nodes that meet the specified filters.
     */
    @GetMapping("/nodes/{id}/children-transactions")
    public ResponseEntity<List<NodeTransaction>> getFilteredTransactions(
            @PathVariable String id,
            @RequestParam(required = false) Double minAmount,
            @RequestParam(required = false) Double maxAmount,
            @RequestParam(required = false) String txnType) {

        List<NodeTransaction> filteredResults = graphService.getFilteredChildTransactions(
                id, minAmount, maxAmount, txnType);

        return ResponseEntity.ok(filteredResults);
    }
}
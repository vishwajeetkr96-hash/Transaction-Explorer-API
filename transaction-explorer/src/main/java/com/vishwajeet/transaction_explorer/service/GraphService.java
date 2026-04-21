package com.vishwajeet.transaction_explorer.service;

import com.vishwajeet.transaction_explorer.exception.CycleDetectedException;
import com.vishwajeet.transaction_explorer.exception.NodeNotFoundException;
import com.vishwajeet.transaction_explorer.model.*;
import com.vishwajeet.transaction_explorer.repository.GraphRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class GraphService {

    private final GraphRepository repository;

    @Autowired
    public GraphService(GraphRepository repository) {
        this.repository = repository;
    }

    /**
     * Primary entry point for node exploration.
     * Orchestrates level calculation, hierarchy building, and data aggregation.
     */
    public NodeResponse exploreNode(String id, int maxDepth) {
        // Validate existence first to avoid unnecessary processing
        GraphNode node = repository.findById(id)
                .orElseThrow(() -> new NodeNotFoundException(id));

        NodeResponse response = new NodeResponse();

        // Map basic fields
        response.setId(node.getId());
        response.setParentId(node.getParentId());
        response.setName(node.getName());
        response.setAccountNumber(node.getAccountNumber());
        response.setTransactions(node.getTransactions());

        // 1. Level Calculation: We pass a new Set for every request to track visited IDs
        // and prevent infinite recursion from malicious or malformed data.
        response.setLevel(calculateLevel(id, new HashSet<>()));

        // 2. Parent Lineage: Build the chain from Root -> Immediate Parent.
        response.setParentChain(buildParentChain(id));

        // 3. Child Discovery: Fetch direct children from our pre-indexed map (O(1) lookup).
        List<GraphNode> directChildren = repository.findChildren(id);
        response.setDirectChildren(directChildren);

        // 4. Transaction Aggregation: Using Java Streams to flatten the list of
        // transactions belonging to all immediate child nodes.
        response.setNextLevelTransactions(directChildren.stream()
                .filter(child -> child.getTransactions() != null)
                .flatMap(child -> child.getTransactions().stream())
                .collect(Collectors.toList()));

        // 5. Node States: Set flags based on graph position.
        response.setRoot(response.getLevel() == 0);
        response.setLeaf(directChildren.isEmpty());

        return response;
    }

    /**
     * Recursive Level Calculation (DFS approach).
     * Rule: Level 0 if parent is null or missing. Level N = 1 + level(parent).
     */
    private int calculateLevel(String id, Set<String> visited) {
        // Safety Check: If we see the same ID twice in one path, we have a cycle.
        if (visited.contains(id)) {
            throw new CycleDetectedException(id);
        }
        visited.add(id);

        GraphNode node = repository.findById(id).orElse(null);

        // Requirement: Treat node as Level 0 if parent is null OR parent not in dataset.
        if (node == null || node.getParentId() == null || !repository.
                exists(node.getParentId())) {
            return 0;
        }

        return 1 + calculateLevel(node.getParentId(), visited);
    }

    /**
     * Path Tracing: Walks up the tree to the root.
     * Uses a Deque (addFirst) to maintain the correct chronological order (Root -> Parent).
     */
    private List<GraphNode> buildParentChain(String id) {
        Deque<GraphNode> chain = new ArrayDeque<>();
        Set<String> visited = new HashSet<>();
        GraphNode current = repository.findById(id).orElse(null);

        while (current != null && current.getParentId() != null) {
            // Cycle detection for iterative loop
            if (visited.contains(current.getId())) {
                throw new CycleDetectedException(id);
            }
            visited.add(current.getId());

            Optional<GraphNode> parent = repository.findById(current.getParentId());

            // If parent exists in JSON, add it to the front of the chain.
            if (parent.isPresent()) {
                chain.addFirst(parent.get());
                current = parent.get();
            } else {
                // If the parent is missing (orphan case), we stop here.
                break;
            }
        }
        return new ArrayList<>(chain);
    }

    /**
     * Bonus 5.4: Transaction Filtering.
     * Demonstrates use of functional predicates for clean filtering logic.
     */
    public List<NodeTransaction> getFilteredChildTransactions(String id, Double min, Double max, String type) {
        return repository.findChildren(id).stream()
                .flatMap(child -> child.getTransactions().stream())
                .filter(t -> (min == null || t.getAmount() >= min))
                .filter(t -> (max == null || t.getAmount() <= max))
                .filter(t -> (type == null || t.getTxnType().equalsIgnoreCase(type)))
                .collect(Collectors.toList());
    }
}
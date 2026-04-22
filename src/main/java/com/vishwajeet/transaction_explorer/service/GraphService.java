package com.vishwajeet.transaction_explorer.service;

import com.vishwajeet.transaction_explorer.exception.CycleDetectedException;
import com.vishwajeet.transaction_explorer.exception.NodeNotFoundException;
import com.vishwajeet.transaction_explorer.model.*;
import com.vishwajeet.transaction_explorer.repository.GraphRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Service layer for graph exploration.
 * Implements Level calculation (Requirement 4.2), Cycle Detection (Requirement 5.3),
 * and Recursive Depth Traversal (Requirement 5.1).
 */
@Service
public class GraphService {

    private final GraphRepository repository;

    @Autowired
    public GraphService(GraphRepository repository) {
        this.repository = repository;
    }

    /**
     * Requirement 4.2 & 5.1: Explore a node and its descendants.
     */
    public NodeResponse exploreNode(String id, int maxDepth) {
        GraphNode node = repository.findById(id)
                .orElseThrow(() -> new NodeNotFoundException(id));

        NodeResponse response = mapToResponse(node);
        response.setParentId(node.getParentId());

        // 1. Level Calculation (Top-level node)
        int rootLevel = calculateLevel(id, new HashSet<>());
        response.setLevel(rootLevel);

        // 2. Parent Lineage
        response.setParentChain(buildParentChain(id));

        // 3. Child Discovery (Direct)
        List<GraphNode> directChildren = repository.findChildren(id);
        response.setDirectChildren(directChildren);

        // 4. Flags
        response.setRoot(rootLevel == 0);
        response.setLeaf(directChildren.isEmpty());

        // 5. Aggregate Next-Level Transactions
        response.setNextLevelTransactions(directChildren.stream()
                .filter(child -> child.getTransactions() != null)
                .flatMap(child -> child.getTransactions().stream())
                .collect(Collectors.toList()));

        // 6. Recursive Children Tree (Bonus 5.1)
        Set<String> visited = new HashSet<>();
        visited.add(id);

        if (maxDepth > 0) {
            response.setChildren(buildChildHierarchy(id, maxDepth - 1, rootLevel, visited));
        } else {
            response.setChildren(new ArrayList<>());
        }

        return response;
    }

    /**
     * Recursive helper to build the tree downwards.
     * Passes currentLevel + 1 to each generation to maintain accurate depth data.
     */
    private List<NodeResponse> buildChildHierarchy(String parentId, int currentDepth, int currentLevel, Set<String> visited) {
        if (currentDepth < 0) return new ArrayList<>();

        List<GraphNode> children = repository.findChildren(parentId);
        List<NodeResponse> childResponses = new ArrayList<>();

        for (GraphNode child : children) {
            if (visited.contains(child.getId())) {
                throw new CycleDetectedException(child.getId());
            }

            Set<String> branchVisited = new HashSet<>(visited);
            branchVisited.add(child.getId());

            NodeResponse childRes = mapToResponse(child);

            // Set calculated level and flags for nested nodes
            int childLevel = currentLevel + 1;
            childRes.setLevel(childLevel);
            childRes.setRoot(false);
            childRes.setLeaf(repository.findChildren(child.getId()).isEmpty());

            if (currentDepth > 0) {
                childRes.setChildren(buildChildHierarchy(child.getId(), currentDepth - 1, childLevel, branchVisited));
            } else {
                childRes.setChildren(new ArrayList<>());
            }

            childResponses.add(childRes);
        }
        return childResponses;
    }

    private int calculateLevel(String id, Set<String> visited) {
        if (visited.contains(id)) throw new CycleDetectedException(id);
        visited.add(id);

        GraphNode node = repository.findById(id).orElse(null);
        if (node == null || node.getParentId() == null || !repository.exists(node.getParentId())) {
            return 0;
        }
        return 1 + calculateLevel(node.getParentId(), visited);
    }

    private List<GraphNode> buildParentChain(String id) {
        Deque<GraphNode> chain = new ArrayDeque<>();
        Set<String> visited = new HashSet<>();
        GraphNode current = repository.findById(id).orElse(null);

        while (current != null && current.getParentId() != null) {
            if (visited.contains(current.getId())) throw new CycleDetectedException(id);
            visited.add(current.getId());

            Optional<GraphNode> parent = repository.findById(current.getParentId());
            if (parent.isPresent()) {
                chain.addFirst(parent.get());
                current = parent.get();
            } else {
                break;
            }
        }
        return new ArrayList<>(chain);
    }

    public List<NodeTransaction> getFilteredChildTransactions(String id, Double min, Double max, String type) {
        return repository.findChildren(id).stream()
                .filter(Objects::nonNull)
                .flatMap(child -> child.getTransactions().stream())
                .filter(t -> (min == null || t.getAmount() >= min))
                .filter(t -> (max == null || t.getAmount() <= max))
                .filter(t -> (type == null || t.getTxnType().equalsIgnoreCase(type)))
                .collect(Collectors.toList());
    }

    private NodeResponse mapToResponse(GraphNode node) {
        NodeResponse res = new NodeResponse();
        res.setId(node.getId());
        res.setName(node.getName());
        res.setAccountNumber(node.getAccountNumber());
        res.setTransactions(node.getTransactions() != null ? node.getTransactions() : new ArrayList<>());
        return res;
    }
}
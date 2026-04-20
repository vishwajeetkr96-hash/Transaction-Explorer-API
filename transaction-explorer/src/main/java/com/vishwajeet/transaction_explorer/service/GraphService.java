package com.vishwajeet.transaction_explorer.service;

import com.vishwajeet.transaction_explorer.model.GraphNode;
import com.vishwajeet.transaction_explorer.model.NodeResponse;
import com.vishwajeet.transaction_explorer.model.NodeTransaction;
import com.vishwajeet.transaction_explorer.repository.GraphRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
public class GraphService {
    @Autowired
    GraphRepository repository;

    public NodeResponse nodeExplorer (String id) {
        GraphNode node = repository.findById(id)
                .orElseThrow(()->new RuntimeException("NODE_NOT_FOUND"));

        NodeResponse response = new NodeResponse();
        response.setId(node.getId());
        response.setName(node.getName());
        response.setAccountNumber(node.getAccountNumber());
        response.setParentId(node.getParentId());
        response.setTransactions(node.getTransactions());

        // Calculate Level & Cycle Detection
        response.setLevel(calculateLevel(id,new HashSet<>()));

        // Build Parent Chain
        response.setParentChain(buildParentChain(id));

        // Direct Children
        List<GraphNode> children = repository.findChildren(id);
        response.setDirectChildren(children);

        // Next Level Transaction
        List<NodeTransaction> nextLevelTxns = new ArrayList<>();
        for (GraphNode child : children) {
            if (child.getTransactions() != null) {
                nextLevelTxns.addAll(child.getTransactions());
            }
        }
        response.setNextLevelTransactions(nextLevelTxns);

        // Flags
        response.setRoot(response.getLevel() == 0);
        response.setLeaf(children.isEmpty());

        return response;



    }

    private List<GraphNode> buildParentChain(String id) {
        List<GraphNode> chain = new ArrayList<>();
        Set<String> visited = new HashSet<>();
        GraphNode current = repository.findById(id).orElse(null);

        while (current != null && current.getParentId() != null) {
            if (visited.contains(current.getId())) throw new RuntimeException("CYCLE_DETECTED");
            visited.add(current.getId());

            GraphNode parent = repository.findById(current.getParentId()).orElse(null);
            if (parent == null) break; // Handles orphan nodes

            chain.add(0, parent); // Adds to start to keep Root -> Child order
            current = parent;
        }
        return chain;
    }

    private int calculateLevel(String id, Set<String> visited) {
        if (visited.contains(id)) throw new RuntimeException("CYCLE_DETECTED");
        visited.add(id);

        GraphNode node = repository.findById(id).orElse(null);
        if (node == null || node.getParentId() == null || !repository.exists(node.getId())) {
            return 0;
        }
        return 1 + calculateLevel(node.getParentId(), visited);
    }
}

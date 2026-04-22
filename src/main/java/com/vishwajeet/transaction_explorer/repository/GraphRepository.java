package com.vishwajeet.transaction_explorer.repository;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.vishwajeet.transaction_explorer.model.GraphNode;
import jakarta.annotation.PostConstruct;
import org.springframework.stereotype.Repository;

import java.io.IOException;
import java.io.InputStream;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
@Repository
public class GraphRepository {
    private final Map<String, GraphNode> nodeMap = new ConcurrentHashMap<>();
    private final Map<String, List<GraphNode>> childrenMap = new ConcurrentHashMap<>();

    @PostConstruct
    public void init() throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        InputStream is = getClass().getResourceAsStream("/transactions-graph-nodes.json");
        if (is == null) throw new IOException("JSON file not found in resources!");

        JsonNode rootNode = mapper.readTree(is);
        List<GraphNode> nodes = mapper.convertValue(rootNode.get("nodes"), new TypeReference<>() {});

        for (GraphNode node : nodes) {
            nodeMap.put(node.getId(), node);
            if (node.getParentId() != null) {
                childrenMap.computeIfAbsent(node.getParentId(), k -> new ArrayList<>()).add(node);
            }
        }
    }

    public Optional<GraphNode> findById(String id) {
        return Optional.ofNullable(nodeMap.get(id));
    }

    public List<GraphNode> findChildren(String parentId) {
        return childrenMap.getOrDefault(parentId, Collections.emptyList());
    }

    public boolean exists(String id) {
        return nodeMap.containsKey(id);
    }
}






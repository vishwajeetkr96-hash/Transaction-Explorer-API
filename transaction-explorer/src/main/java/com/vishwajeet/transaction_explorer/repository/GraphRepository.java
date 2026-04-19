package com.vishwajeet.transaction_explorer.repository;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.vishwajeet.transaction_explorer.model.GraphNode;
import jakarta.annotation.PostConstruct;
import org.springframework.stereotype.Repository;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
@Repository
public class GraphRepository {
    private final Map<String, GraphNode> nodeMap = new ConcurrentHashMap<>();
    private final Map<String, List<GraphNode>> childMap = new ConcurrentHashMap<>();

    @PostConstruct
    public void init() throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        InputStream is = getClass().getResourceAsStream("/transactions-graph-nodes.json");
        if (is == null) throw new IOException("JSON file not found in resources!");


}

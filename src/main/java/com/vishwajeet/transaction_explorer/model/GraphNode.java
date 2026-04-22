package com.vishwajeet.transaction_explorer.model;

import lombok.Data;

import java.util.List;
@Data
public class GraphNode {
    private String id;
    private String parentId;
    private String name;
    private String accountNumber;
    private List<NodeTransaction> transactions;
}

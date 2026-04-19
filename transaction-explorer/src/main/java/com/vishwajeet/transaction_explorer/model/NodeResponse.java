package com.vishwajeet.transaction_explorer.model;

import lombok.Data;

import java.util.List;

@Data
public class NodeResponse {
    private String id;
    private String parentId;
    private String name;
    private String accountNumber;
    private int level;
    private List<GraphNode> parentChain;
    private List<GraphNode> directChildren;
    private List<NodeTransaction> transactions;
    private List<NodeTransaction> nextLevelTransactions;
    private boolean isRoot;
    private boolean isLeaf;

}

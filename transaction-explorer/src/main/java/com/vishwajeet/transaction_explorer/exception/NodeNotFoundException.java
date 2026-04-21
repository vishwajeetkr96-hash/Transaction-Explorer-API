package com.vishwajeet.transaction_explorer.exception;

import com.vishwajeet.transaction_explorer.exception.GraphException;

public class NodeNotFoundException extends GraphException {
    public NodeNotFoundException(String id) {
        super("Graph node " + id + " does not exist");
    }
}
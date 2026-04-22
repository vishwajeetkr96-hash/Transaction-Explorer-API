package com.vishwajeet.transaction_explorer.model;

import lombok.Data;

@Data
public class NodeTransaction {
    private String txnId;
    private String direction;
    private String txnType;
    private double amount;
    private String currency;
    private String timestamp;
    private String description;


}

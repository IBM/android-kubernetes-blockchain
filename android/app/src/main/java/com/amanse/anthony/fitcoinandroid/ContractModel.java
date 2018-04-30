package com.amanse.anthony.fitcoinandroid;

import com.google.gson.annotations.SerializedName;

public class ContractModel {

    @SerializedName("id")
    String contractId;
    String sellerId;
    String userId;
    String productId;
    String productName;
    int quantity;
    int cost;
    String state;

    public ContractModel(String contractId, String sellerId, String userId, String productId, String productName, int quantity, int cost, String state) {
        this.contractId = contractId;
        this.sellerId = sellerId;
        this.userId = userId;
        this.productId = productId;
        this.productName = productName;
        this.quantity = quantity;
        this.cost = cost;
        this.state = state;
    }

    public String getContractId() {
        return contractId;
    }

    public String getSellerId() {
        return sellerId;
    }

    public String getUserId() {
        return userId;
    }

    public String getProductId() {
        return productId;
    }

    public String getProductName() {
        return productName;
    }

    public int getQuantity() {
        return quantity;
    }

    public int getCost() {
        return cost;
    }

    public String getState() {
        return state;
    }
}

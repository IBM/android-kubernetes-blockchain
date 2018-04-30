package com.amanse.anthony.fitcoinandroid;

import com.google.gson.annotations.SerializedName;

public class ShopItemModel {

    @SerializedName("sellerid")
    String sellerId;
    @SerializedName("productid")
    String productId;
    @SerializedName("name")
    String productName;
    @SerializedName("count")
    int quantityLeft;
    int price;

    public ShopItemModel(String sellerId, String productId, String productName, int quantityLeft, int price) {
        this.sellerId = sellerId;
        this.productId = productId;
        this.productName = productName;
        this.quantityLeft = quantityLeft;
        this.price = price;
    }

    public String getProductId() {
        return productId;
    }

    public String getSellerId() {
        return sellerId;
    }

    public String getProductName() {
        return productName;
    }

    public int getQuantityLeft() {
        return quantityLeft;
    }

    public int getPrice() {
        return price;
    }
}

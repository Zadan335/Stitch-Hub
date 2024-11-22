package com.example.stitchhub;

public class CatalogProduct {
    private String designCode;
    private String gender;
    private String price; // Keep it as String
    private String imageUrl;

    public CatalogProduct() {
        // Default constructor required for calls to DataSnapshot.getValue(CatalogProduct.class)
    }

    public CatalogProduct(String designCode, String gender, String price, String imageUrl) {
        this.designCode = designCode;
        this.gender = gender;
        this.price = price;
        this.imageUrl = imageUrl;
    }

    public String getDesignCode() {
        return designCode;
    }

    public String getGender() {
        return gender;
    }

    public String getPrice() {
        return price;
    }

    public String getImageUrl() {
        return imageUrl;
    }
}
package com.example.stitchhub;

import static com.example.stitchhub.OrderStatus.PENDING;

import java.util.List;

public class Order {
    private String tailorUid;
    private List<CatalogProduct> products;
    private String address;
    private String contact;
    private String estimatedDelivery;
    private String orderId;
    private double totalPrice;
    private double shirtLength;
    private double shoulder;
    private double sleeves;
    private double chest;
    private double trouserLength;
    private OrderStatus orderStatus;


    // Constructor
    public Order(String tailorUid, List<CatalogProduct> products, String address,
                 String contact, String estimatedDelivery, double totalPrice,
                 double shirtLength, double shoulder, double sleeves,
                 double chest, double trouserLength, OrderStatus orderStatus
                 ) { // Add customerUid to constructor
        this.tailorUid = tailorUid;
        this.products = products;
        this.address = address;
        this.contact = contact;
        this.estimatedDelivery = estimatedDelivery;
        this.totalPrice = totalPrice;
        this.shirtLength = shirtLength;
        this.shoulder = shoulder;
        this.sleeves = sleeves;
        this.chest = chest;
        this.trouserLength = trouserLength;
        this.orderStatus = orderStatus;

    }
    public Order() {
        // Default constructor required for calls to DataSnapshot.getValue(Order.class)
    }


    // Getters and setters
    public String getTailorUid() {
        return tailorUid;
    }

    public void setTailorUid(String tailorUid) {
        this.tailorUid = tailorUid;
    }

    public List<CatalogProduct> getProducts() {
        return products;
    }

    public void setProducts(List<CatalogProduct> products) {
        this.products = products;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getContact() {
        return contact;
    }

    public void setContact(String contact) {
        this.contact = contact;
    }

    public String getEstimatedDelivery() {
        return estimatedDelivery;
    }

    public void setEstimatedDelivery(String estimatedDelivery) {
        this.estimatedDelivery = estimatedDelivery;
    }

    public double getTotalPrice() {
        return totalPrice;
    }

    public void setTotalPrice(double totalPrice) {
        this.totalPrice = totalPrice;
    }

    public double getShirtLength() {
        return shirtLength;
    }
    public OrderStatus getStatus() {
        return orderStatus;
    }
    public void setStatus(OrderStatus status) {
        this.orderStatus=status;

    }

    public void setShirtLength(double shirtLength) {
        this.shirtLength = shirtLength;
    }

    public double getShoulder() {
        return shoulder;
    }

    public void setShoulder(double shoulder) {
        this.shoulder = shoulder;
    }

    public double getSleeves() {
        return sleeves;
    }

    public void setSleeves(double sleeves) {
        this.sleeves = sleeves;
    }

    public double getChest() {
        return chest;
    }

    public void setChest(double chest) {
        this.chest = chest;
    }

    public double getTrouserLength() {
        return trouserLength;
    }


    public void setTrouserLength(double trouserLength) {
        this.trouserLength = trouserLength;
    }

    // Getter and setter for order status
    public OrderStatus getOrderStatus() {
        return orderStatus != null ? orderStatus : PENDING;
    }

    public void setOrderStatus(OrderStatus orderStatus) {
        this.orderStatus = orderStatus;
    }



}
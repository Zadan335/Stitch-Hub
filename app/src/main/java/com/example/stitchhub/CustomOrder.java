package com.example.stitchhub;

public class CustomOrder {
    private String orderId;
    private String customerId;
    private String tailorId; // Can be assigned later by the tailor
    private String designUri; // URI for uploaded design
    private String appointmentDate; // Selected date for the appointment
    private double priceRange; // Customer's proposed price range
    private String status; // "Pending", "Accepted", "Rejected"
    private String address; // Address where the customer wants the service

    // Empty constructor for Firebase
    public CustomOrder() {
    }

    // Constructor used in CustomerOrderFragment
    public CustomOrder(String orderId, String customerId, String designUri, String appointmentDate, double priceRange, String address) {
        this.orderId = orderId;
        this.customerId = customerId;
        this.designUri = designUri;
        this.appointmentDate = appointmentDate;
        this.priceRange = priceRange;
        this.status = "Pending"; // Default status
        this.address = address; // Add address to the order
    }

    // Getters and Setters
    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    public String getCustomerId() {
        return customerId;
    }

    public void setCustomerId(String customerId) {
        this.customerId = customerId;
    }

    public String getTailorId() {
        return tailorId;
    }

    public void setTailorId(String tailorId) {
        this.tailorId = tailorId;
    }

    public String getDesignUri() {
        return designUri;
    }

    public void setDesignUri(String designUri) {
        this.designUri = designUri;
    }

    public String getAppointmentDate() {
        return appointmentDate;
    }

    public void setAppointmentDate(String appointmentDate) {
        this.appointmentDate = appointmentDate;
    }

    public double getPriceRange() {
        return priceRange;
    }

    public void setPriceRange(double priceRange) {
        this.priceRange = priceRange;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }
}
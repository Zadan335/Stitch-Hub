package com.example.stitchhub;

public class Tailor {

    private String name;
    private String phone;
    private String address;
    private Double latitude;
    private Double longitude;
    private String email;
    private String profilePictureUrl;
    private String uid; // Unique identifier for the tailor

    // Default constructor required for calls to DataSnapshot.getValue(Tailor.class)
    public Tailor() {
    }

    public Tailor(String name, String phone, String address, Double latitude, Double longitude, String email, String profilePictureUrl, String uid) {
        this.name = name;
        this.phone = phone;
        this.address = address;
        this.latitude = latitude;
        this.longitude = longitude;
        this.email = email;
        this.profilePictureUrl = profilePictureUrl;
        this.uid = uid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public Double getLatitude() {
        return latitude;
    }

    public void setLatitude(Double latitude) {
        this.latitude = latitude;
    }

    public Double getLongitude() {
        return longitude;
    }

    public void setLongitude(Double longitude) {
        this.longitude = longitude;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getProfilePictureUrl() {
        return profilePictureUrl;
    }

    public void setProfilePictureUrl(String profilePictureUrl) {
        this.profilePictureUrl = profilePictureUrl;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }
}
package com.example.stitchhub;

public class PortfolioItem {
    private String id;
    private String name;
    private String description;
    private String imageUrl;

    public PortfolioItem() {
        // Default constructor required for calls to DataSnapshot.getValue(PortfolioItem.class)
    }

    public PortfolioItem(String id, String name, String description, String imageUrl) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.imageUrl = imageUrl;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    // Getters and setters
    // ...
}
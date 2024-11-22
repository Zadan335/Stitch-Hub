package com.example.stitchhub;

public class ChatThread {
    private String receiverUid;
    private String lastMessage;
    private String email;
    private String profileImageUrl; // Added field for the profile image URL

    public ChatThread() {
        // Default constructor
    }

    public ChatThread(String receiverUid, String lastMessage, String email, String profileImageUrl) {
        this.receiverUid = receiverUid;
        this.lastMessage = lastMessage;
        this.email = email;
        this.profileImageUrl = profileImageUrl;
    }

    public String getReceiverUid() {
        return receiverUid;
    }

    public String getLastMessage() {
        return lastMessage;
    }

    public String getEmail() {
        return email;
    }

    public String getProfileImageUrl() {
        return profileImageUrl;
    }
}
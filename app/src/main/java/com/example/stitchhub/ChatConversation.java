package com.example.stitchhub;

public class ChatConversation {
    private String senderUid;
    private String receiverUid;
    private String senderEmail;
    private String receiverEmail;
    private String lastMessage;
    private long timestamp;
    // No-argument constructor required for Firebase
    public ChatConversation() {
    }

    public ChatConversation(String senderUid, String receiverUid, String senderEmail, String receiverEmail, String lastMessage, long timestamp) {
        this.senderUid = senderUid;
        this.receiverUid = receiverUid; // Initialize receiverUid
        this.senderEmail = senderEmail;
        this.receiverEmail = receiverEmail; // Initialize receiverEmail
        this.lastMessage = lastMessage;
        this.timestamp = timestamp;
    }
    public String getSenderUid() {
        return senderUid;
    }

    public void setSenderUid(String senderUid) {
        this.senderUid = senderUid;
    }

    public String getReceiverUid() {
        return receiverUid;
    }

    public void setReceiverUid(String receiverUid) {
        this.receiverUid = receiverUid;
    }

    public String getSenderEmail() {
        return senderEmail;
    }

    public void setSenderEmail(String senderEmail) {
        this.senderEmail = senderEmail;
    }

    public String getReceiverEmail() {
        return receiverEmail;
    }

    public void setReceiverEmail(String receiverEmail) {
        this.receiverEmail = receiverEmail;
    }

    public String getLastMessage() {
        return lastMessage;
    }

    public void setLastMessage(String lastMessage) {
        this.lastMessage = lastMessage;
    }
}
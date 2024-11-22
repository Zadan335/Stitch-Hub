package com.example.stitchhub;

public class Chat {
    private String messageId;
    private String messageText;
    private String senderId;
    private String receiverId; // Added to identify who is the receiver
    private String senderEmail; // Sender's email
    private String senderProfilePic; // Sender's profile picture URL
    private long timestamp; // To keep track of message time

    private String receiverEmail; // Receiver's email

    // Add this in the constructor
    public Chat(String messageId, String messageText, String senderId, String receiverId, String senderEmail, String senderProfilePic, long timestamp, String receiverEmail) {
        this.messageId = messageId;
        this.messageText = messageText;
        this.senderId = senderId;
        this.receiverId = receiverId;
        this.senderEmail = senderEmail;
        this.senderProfilePic = senderProfilePic;
        this.timestamp = timestamp;
        this.receiverEmail = receiverEmail; // Initialize receiverEmail
    }

    // Getter for receiverEmail
    public String getReceiverEmail() {
        return receiverEmail;
    }
    public Chat() {
        // Default constructor required for calls to DataSnapshot.getValue(Chat.class)
    }
    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }
    public String setReceiverEmail(String receiverEmail) {
        this.receiverEmail=receiverEmail;
        return receiverEmail;
    }

    public String getSenderProfilePic() {
        return senderProfilePic;
    }

    public void setSenderProfilePic(String senderProfilePic) {
        this.senderProfilePic = senderProfilePic;
    }

    public String getSenderEmail() {
        return senderEmail;
    }

    public void setSenderEmail(String senderEmail) {
        this.senderEmail = senderEmail;
    }

    public String getReceiverId() {
        return receiverId;
    }

    public void setReceiverId(String receiverId) {
        this.receiverId = receiverId;
    }

    public String getSenderId() {
        return senderId;
    }

    public void setSenderId(String senderId) {
        this.senderId = senderId;
    }

    public String getMessageText() {
        return messageText;
    }

    public void setMessageText(String messageText) {
        this.messageText = messageText;
    }

    public String getMessageId() {
        return messageId;
    }

    public void setMessageId(String messageId) {
        this.messageId = messageId;
    }

    // Getters and Setters
    // ...
}
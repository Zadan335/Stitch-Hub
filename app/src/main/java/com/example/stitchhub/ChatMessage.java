package com.example.stitchhub;

public class ChatMessage {
    private String senderUid;
    private String message;
    private long timestamp;

    public ChatMessage() {
        // Required for Firebase
    }

    public ChatMessage(String senderUid, String message, long timestamp) {
        this.senderUid = senderUid;
        this.message = message;
        this.timestamp = timestamp;
    }

    public String getSenderUid() {
        return senderUid;
    }

    public String getMessage() {
        return message;
    }

    public long getTimestamp() {
        return timestamp;
    }
}
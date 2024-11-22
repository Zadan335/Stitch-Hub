package com.example.stitchhub;

public class InboxMessage {
    private String sender;
    private String message;
    private long timestamp;

    public InboxMessage() {
        // Default constructor required for calls to DataSnapshot.getValue(InboxMessage.class)
    }

    public InboxMessage(String sender, String message, long timestamp) {
        this.sender = sender;
        this.message = message;
        this.timestamp = timestamp;
    }

    public String getSender() {
        return sender;
    }

    public String getMessage() {
        return message;
    }

    public long getTimestamp() {
        return timestamp;
    }
}


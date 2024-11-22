package com.example.stitchhub;

public class Message {
    private String text;           // The text of the message
    private String sender;         // Email of the sender
    private long timestamp;        // Timestamp of the message

    public Message(String sender, String text, long timestamp) {

        this.sender = sender;
        this.text = text;
        this.timestamp = timestamp;
    }

    public String getText() {
        return text;
    }

    public String getSender() {
        return sender;
    }

    public long getTimestamp() {
        return timestamp;
    }
}
package com.example.stitchhub;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class ChatConversationc extends Fragment {
    private String lastMessage;
    private String senderEmail;
    private long timestamp;
    private String receiverUid;  // Add this field

    public ChatConversationc() {
        // Default constructor required for calls to DataSnapshot.getValue(ChatConversation.class)
    }

    public ChatConversationc(String lastMessage,String receiverUid,String reciever, long timestamp) {
        this.lastMessage = lastMessage;
        this.senderEmail = senderEmail;
        this.timestamp = timestamp;
        this.receiverUid = receiverUid;  // Initialize the field
    }

    public String getLastMessage() {
        return lastMessage;
    }

    public String getSenderEmail() {
        return senderEmail;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public String getReceiverUid() {
        return receiverUid;
    }
}
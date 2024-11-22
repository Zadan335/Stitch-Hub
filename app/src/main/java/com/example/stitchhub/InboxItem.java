package com.example.stitchhub;
public class InboxItem {

        private String chatId;
        private String senderEmail;
        private String lastMessage;
        private Long timestamp;
        private String tailorUid; // Add this field

        public InboxItem(String chatId, String senderEmail, String lastMessage, Long timestamp, String tailorUid) {
            this.chatId = chatId;
            this.senderEmail = senderEmail;
            this.lastMessage = lastMessage;
            this.timestamp = timestamp;
            this.tailorUid = tailorUid; // Initialize the new field
        }

        // Getters
        public String getChatId() {
            return chatId;
        }

        public String getSenderEmail() {
            return senderEmail;
        }

        public String getLastMessage() {
            return lastMessage;
        }

        public Long getTimestamp() {
            return timestamp;
        }

        public String getTailorUid() {
            return tailorUid; // Getter for tailorUid
        }
    }
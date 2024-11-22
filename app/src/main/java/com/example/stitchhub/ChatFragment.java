package com.example.stitchhub;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
public class ChatFragment extends Fragment {
private DatabaseReference chatRef;
private String chatId;
private RecyclerView messagesRecyclerView;
private MessageAdapter messagesAdapter;
private List<Message> messagesList;
private EditText editTextMessage;
private Button buttonSend;
private String tailorUid;

@Override
public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_chat, container, false);

        // Get chatId and tailorUid from arguments
        Bundle args = getArguments();
        if (args != null) {
        chatId = args.getString("CHAT_ID");
        tailorUid = args.getString("RECEIVER_UID"); // Retrieve tailor UID
        }

        Log.d("ChatFragment", "Chat ID: " + chatId);
        Log.d("ChatFragment", "Tailor UID: " + tailorUid); // Log for debugging

        if (chatId == null || tailorUid == null) {
        Toast.makeText(getContext(), "Error: Chat ID or Tailor UID is null", Toast.LENGTH_SHORT).show();
        return view; // Return early
        }

        // Initialize RecyclerView and Adapter
        messagesRecyclerView = view.findViewById(R.id.recyclerViewMessages);
        messagesList = new ArrayList<>();
        messagesAdapter = new MessageAdapter(messagesList);
        messagesRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        messagesRecyclerView.setAdapter(messagesAdapter); // Set adapter here

        // Reference to chat messages
        chatRef = FirebaseDatabase.getInstance().getReference("chats").child(chatId).child("messages");

        // Load messages
        loadMessages();

        // Initialize EditText and Button
        editTextMessage = view.findViewById(R.id.editTextMessage);
        buttonSend = view.findViewById(R.id.buttonSend);

        // Send button listener
        buttonSend.setOnClickListener(v -> sendMessage());

        return view;
        }

private void loadMessages() {
        chatRef.addValueEventListener(new ValueEventListener() {
@Override
public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
        messagesList.clear();
        if (dataSnapshot.exists()) {
        for (DataSnapshot messageSnapshot : dataSnapshot.getChildren()) {
        String senderEmail = messageSnapshot.child("sender").getValue(String.class);
        String text = messageSnapshot.child("text").getValue(String.class);
        Long timestamp = messageSnapshot.child("timestamp").getValue(Long.class);

        // Check for null values
        if (senderEmail != null && text != null && timestamp != null) {
        Message messageItem = new Message(senderEmail, text, timestamp);
        messagesList.add(messageItem);
        }
        }
        messagesAdapter.notifyDataSetChanged();
        if (!messagesList.isEmpty()) {
        messagesRecyclerView.scrollToPosition(messagesList.size() - 1);
        }
        }
        }

@Override
public void onCancelled(@NonNull DatabaseError databaseError) {
        Log.e("ChatFragment", "Error loading messages: " + databaseError.getMessage());
        }
        });
        }

private void sendMessage() {
        String messageText = editTextMessage.getText().toString().trim();
        if (!messageText.isEmpty()) {
        String senderEmail = FirebaseAuth.getInstance().getCurrentUser().getEmail();
        long timestamp = System.currentTimeMillis();
        Message message = new Message(senderEmail, messageText, timestamp);

        // Clear EditText
        editTextMessage.setText("");
        editTextMessage.clearFocus(); // Remove focus from EditText

        chatRef.push().setValue(message).addOnCompleteListener(task -> {
        if (task.isSuccessful()) {
        updateInbox(chatId, messageText, senderEmail, timestamp, tailorUid);
        } else {
        Toast.makeText(getContext(), "Failed to send message", Toast.LENGTH_SHORT).show();
        }
        });
        } else {
        Toast.makeText(getContext(), "Please enter a message", Toast.LENGTH_SHORT).show();
        }
        }

private void updateInbox(String chatId, String lastMessage, String sender, long timestamp, String tailorUid) {
        String userUid = FirebaseAuth.getInstance().getCurrentUser().getUid(); // Get the current user's UID

        // Prepare the inbox entry data
        Map<String, Object> inboxEntry = new HashMap<>();
        inboxEntry.put("lastMessage", lastMessage);
        inboxEntry.put("sender", sender);
        inboxEntry.put("timestamp", timestamp);

        // Update inbox for the current user (customer)
        updateUserInbox(userUid, chatId, inboxEntry);

        // Update inbox for the tailor
        updateUserInbox(tailorUid, chatId, inboxEntry);
        }

private void updateUserInbox(String userUid, String chatId, Map<String, Object> inboxEntry) {
        DatabaseReference inboxRef = FirebaseDatabase.getInstance().getReference("inbox").child(sanitizePath(userUid)).child(sanitizePath(chatId));

        inboxRef.setValue(inboxEntry).addOnCompleteListener(task -> {
        if (task.isSuccessful()) {
        Log.d("ChatFragment", "Inbox updated successfully for user: " + userUid);
        } else {
        Log.e("ChatFragment", "Failed to update inbox for user: " + userUid + ". Error: " + task.getException().getMessage());
        }
        });
        }

private String sanitizePath(String input) {
        // Replace characters that are not allowed in Firebase Database paths
        return input.replace(".", "_dot_")
        .replace("@", "_at_")
        .replace("#", "_hash_")
        .replace("$", "_dollar_")
        .replace("[", "_lbracket_")
        .replace("]", "_rbracket_");
        }
        }
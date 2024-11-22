package com.example.stitchhub;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

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
import java.util.List;


public class ChatInboxFragment extends Fragment {
    private RecyclerView recyclerViewInbox;
   // private ChatInboxAdapter inboxAdapter;
    private List<Chat> inboxList; // List to hold active conversations

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        inboxList = new ArrayList<>();
        //inboxAdapter = new ChatInboxAdapter(inboxList, this::onChatClick);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_inbox, container, false);

        recyclerViewInbox = view.findViewById(R.id.recyclerViewInbox);
        recyclerViewInbox.setLayoutManager(new LinearLayoutManager(getContext()));
        //recyclerViewInbox.setAdapter(inboxAdapter);

        loadInboxMessages();

        return view;
    }

    // Function to load inbox messages from the database
    private void loadInboxMessages() {
        String currentUserUid = FirebaseAuth.getInstance().getCurrentUser().getUid();

        DatabaseReference inboxRef = FirebaseDatabase.getInstance().getReference("chats").child(currentUserUid);
        inboxRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                inboxList.clear(); // Clear existing inbox

                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    // Iterate through chats for the current user
                    for (DataSnapshot chatSnapshot : snapshot.getChildren()) {
                        Chat chat = chatSnapshot.getValue(Chat.class);
                        if (chat != null) {
                            inboxList.add(chat); // Add chat to the inbox list
                        }
                    }
                }

                //  inboxAdapter.notifyDataSetChanged(); // Notify the adapter of data changes
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Handle possible errors.
            }
        });
    }

    // Handle chat click to open the chat fragment
    private void onChatClick(Chat chat) {
        // Open the ChatFragment and pass the receiver's UID
        ChatFragment chatFragment = new ChatFragment();
        Bundle bundle = new Bundle();
        bundle.putString("RECEIVER_UID", chat.getReceiverId());
        bundle.putString("RECEIVER_EMAIL", chat.getSenderEmail());
        chatFragment.setArguments(bundle);

        // Replace the current fragment with ChatFragment
        getActivity().getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragment_container, chatFragment)
                .addToBackStack(null)
                .commit();
    }
}
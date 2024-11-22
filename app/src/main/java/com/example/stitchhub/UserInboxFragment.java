package com.example.stitchhub;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;
public class UserInboxFragment extends Fragment {
    private RecyclerView inboxRecyclerView;
    private List<InboxItem> inboxList;
    private InboxAdapter inboxAdapter;
    private DatabaseReference inboxRef;
    private BottomNavigationView bottomNavigationView;
    private String userUid;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_user_inbox, container, false);

        inboxRecyclerView = view.findViewById(R.id.inboxRecyclerView);
        inboxRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        inboxList = new ArrayList<>();
        inboxAdapter = new InboxAdapter(inboxList, this::openChatScreen);
        inboxRecyclerView.setAdapter(inboxAdapter);

        userUid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        inboxRef = FirebaseDatabase.getInstance().getReference("inbox").child(userUid);

        loadInbox();
        bottomNavigationView = view.findViewById(R.id.bottom_navigation);
        setupBottomNavigation();

        return view;

    }

    private void loadInbox() {
        inboxRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                inboxList.clear();
                Log.d("UserInbox", "Inbox data loaded: " + dataSnapshot.getChildrenCount() + " chats found.");

                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    String chatId = snapshot.getKey(); // Get the chat ID
                    String lastMessage = snapshot.child("lastMessage").getValue(String.class);
                    String senderEmail = snapshot.child("sender").getValue(String.class);
                    Long timestamp = snapshot.child("timestamp").getValue(Long.class);
                    String tailorUid = snapshot.getKey(); // Use the key as tailorUid since it appears to be a chat ID.

                    // Log retrieved chat details
                    Log.d("UserInbox", "ChatId: " + chatId + ", LastMessage: " + lastMessage + ", Sender: " + senderEmail + ", Timestamp: " + timestamp);

                    if (lastMessage != null && senderEmail != null && timestamp != null) {
                        InboxItem item = new InboxItem(chatId, senderEmail, lastMessage, timestamp, tailorUid);
                        inboxList.add(item);
                    } else {
                        Log.e("UserInbox", "Incomplete data for chatId: " + chatId);
                    }
                }
                inboxAdapter.notifyDataSetChanged(); // Notify adapter after loading all inbox items
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e("UserInbox", "Error loading inbox: " + databaseError.getMessage());
            }
        });
    }
    private void setupBottomNavigation() {
        bottomNavigationView.setOnNavigationItemSelectedListener(item -> {
            int itemId = item.getItemId(); // Get the selected item ID
            Fragment selectedFragment = null;
            // Using if-else for navigation handling
            if (itemId == R.id.navigation_home) {
                selectedFragment = new CustomerHomeFragment();
                Log.d("InboxFragment", "Home selected");
                // You can use FragmentManager to replace fragments if needed
            } else if (itemId == R.id.navigation_explore_tailors) {
                selectedFragment = new Explore();

                Log.d("InboxFragment", "Orders selected");
            } else if (itemId == R.id.nav_inbox) {
                // Already on the inbox screen
                Log.d("InboxFragment", "Inbox selected (already here)");
            } else if (itemId == R.id.navigation_my_orders) {
                selectedFragment = new orders();

                Log.d("InboxFragment", "Profile selected");
            }
            else {
                return false; // If no recognized item is selected, return false
            }
            if (selectedFragment != null) {

                    getActivity().getSupportFragmentManager().beginTransaction()
                            .setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out) // Set fade-in and fade-out animation
                            .replace(R.id.fragment_container, selectedFragment)
                            .addToBackStack(null)
                            .commit();

            }
            return true; // Return true to indicate the navigation was handled
        });
    }
    private void openChatScreen(InboxItem inboxItem) {
        ChatFragment chatFragment = new ChatFragment();
        Bundle args = new Bundle();
        args.putString("CHAT_ID", inboxItem.getChatId());
        args.putString("RECEIVER_UID", inboxItem.getTailorUid()); // Pass the tailor UID
        chatFragment.setArguments(args);

        getParentFragmentManager().beginTransaction()
                .replace(R.id.fragment_container, chatFragment)
                .addToBackStack(null)
                .commit();
    }
}
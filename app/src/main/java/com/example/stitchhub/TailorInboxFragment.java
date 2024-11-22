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


public class TailorInboxFragment extends Fragment {
    private RecyclerView inboxRecyclerView;
    private List<InboxItem> inboxList;
    private InboxAdapter inboxAdapter;
    BottomNavigationView bottomNavigationView;
    private DatabaseReference inboxRef;
    private String tailorUid;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_inbox, container, false);

        inboxRecyclerView = view.findViewById(R.id.recyclerViewInbox);
        inboxRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
bottomNavigationView=view.findViewById(R.id.bottom_navigation) ;
inboxList = new ArrayList<>();
        inboxAdapter = new InboxAdapter(inboxList, this::openChatScreen);
        inboxRecyclerView.setAdapter(inboxAdapter);

        // Get current tailor UID from FirebaseAuth
        tailorUid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        Log.d("TailorInbox", "Current tailor UID: " + tailorUid);
        inboxRef = FirebaseDatabase.getInstance().getReference("inbox").child(tailorUid);

        loadInbox();
        bottomNavigationView.setOnNavigationItemSelectedListener(item -> {
            Fragment selectedFragment = null;
            int itemId = item.getItemId();

            // Log the item ID to check which item is selected
            Log.d("BottomNavigation", "Selected Item ID: " + itemId);

            if (itemId == R.id.nav_catalog) {
                selectedFragment = new CatalogFragment();
            } else if (itemId == R.id.nav_home) {
                selectedFragment = new homet();
            } else if (itemId == R.id.nav_order) {
                selectedFragment = new tailororder();
            } else if (itemId == R.id.nav_inbox) {
                selectedFragment = new TailorInboxFragment();
                return true; // Prevents reloading the same fragment
            }else if (itemId == R.id.nav_Custorder) {
                selectedFragment = new TailorOrderFragment();
                return true; // Prevents reloading the same fragment
            }

            // Log if a fragment is selected
            if (selectedFragment != null) {
                Log.d("BottomNavigation", "Loading Fragment: " + selectedFragment.getClass().getSimpleName());
                loadFragment(selectedFragment);
            } else {
                Log.d("BottomNavigation", "No Fragment Selected");
            }

            return true;
        });

        return view;
    }

    private void loadInbox() {
        inboxRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                inboxList.clear(); // Clear previous entries

                if (!dataSnapshot.exists()) {
                    Log.e("TailorInbox", "No inbox found.");
                    return;
                }

                // Loop through each chat entry in the inbox
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    String chatId = snapshot.getKey(); // Chat ID
                    String lastMessage = snapshot.child("lastMessage").getValue(String.class);
                    String senderEmail = snapshot.child("sender").getValue(String.class);
                    Long timestamp = snapshot.child("timestamp").getValue(Long.class);

                    // Ensure all values are not null
                    if (chatId != null && lastMessage != null && senderEmail != null && timestamp != null) {
                        // Create and add InboxItem to the list
                        InboxItem inboxItem = new InboxItem(chatId, senderEmail, lastMessage, timestamp, tailorUid);
                        inboxList.add(inboxItem);
                    } else {
                        Log.e("TailorInbox", "Incomplete data for chatId: " + chatId);
                    }
                }
                inboxAdapter.notifyDataSetChanged(); // Notify adapter after loading all inbox items
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e("TailorInbox", "Error loading inbox: " + databaseError.getMessage());
            }
        });
    }
    private void loadFragment(Fragment fragment) {
        // Load the selected fragment into the container
        getActivity().getSupportFragmentManager().beginTransaction()
                .setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out) // Set fade-in and fade-out animation
                .replace(R.id.fragment_container, fragment)
                .addToBackStack(null)
                .commit();
    }
    private void openChatScreen(InboxItem inboxItem) {
        ChatFragment chatFragment = new ChatFragment();
        Bundle args = new Bundle();
        args.putString("CHAT_ID", inboxItem.getChatId());
        args.putString("RECEIVER_UID", inboxItem.getSenderEmail()); // Pass the sender email as receiver UID
        chatFragment.setArguments(args);

        getParentFragmentManager().beginTransaction()
                .replace(R.id.fragment_container, chatFragment)
                .addToBackStack(null)
                .commit();
    }
}

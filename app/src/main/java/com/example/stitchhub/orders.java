package com.example.stitchhub;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;


public class orders extends Fragment {

    private RecyclerView recyclerViewOrders;
    private OrdersAdapter adapter;
    private List<Order> orderList;
    private static final String TAG = "OrdersFragment"; // Tag for logging
    private String currentUserId; // This will hold the current user ID
    private BottomNavigationView bottomNavigationView; // Declare BottomNavigationView

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_orders, container, false);

        // Initialize RecyclerView
        recyclerViewOrders = view.findViewById(R.id.recyclerViewOrders);
        recyclerViewOrders.setLayoutManager(new LinearLayoutManager(getContext()));

        // Initialize the order list and adapter
        orderList = new ArrayList<>();
        adapter = new OrdersAdapter(orderList, getContext(), order -> {
            // Handle the order click here, e.g., show order details
        });
        recyclerViewOrders.setAdapter(adapter);

        // Initialize BottomNavigationView and set up navigation listener
        bottomNavigationView = view.findViewById(R.id.bottom_navigation);
        setupBottomNavigation();

        // Load the current user ID and then load orders
        loadCurrentUserId();
        return view;
    }

    private void setupBottomNavigation() {
        bottomNavigationView.setOnNavigationItemSelectedListener(item -> {
            int itemId = item.getItemId(); // Get the selected item ID
            Fragment selectedFragment = null;

            // Using if-else for navigation handling
            if (itemId == R.id.navigation_home) {
                selectedFragment = new CustomerHomeFragment();

                // Handle navigation to home
                // You can start a new fragment or activity here
                Log.d(TAG, "Home selected");
            } else if (itemId == R.id.navigation_explore_tailors) {
                selectedFragment = new Explore();

                Log.d(TAG, "Orders selected (already here)");
            } else if (itemId == R.id.navigation_inbox) {                selectedFragment = new UserInboxFragment();

                // Handle navigation to profile
                Log.d(TAG, "Profile selected");
            } else if (itemId == R.id.navigation_my_orders) {
                // Navigate to settings screen
                // Handle navigation to settings
                Log.d(TAG, "Settings selected");
            } else {
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

    private void loadCurrentUserId() {
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser(); // Get current user
        if (currentUser != null) {
            currentUserId = currentUser.getUid(); // Get the user ID
            Log.d(TAG, "Current User ID: " + currentUserId); // Log the current user ID
            loadOrders(); // Load orders after getting the user ID
        } else {
            Log.w(TAG, "No user is signed in."); // Log if no user is signed in
        }
    }

    private void loadOrders() {
        Log.d(TAG, "Loading orders from Firebase..."); // Log when starting to load orders
        DatabaseReference ordersRef = FirebaseDatabase.getInstance().getReference("orders");

        // Listening for order data
        ordersRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                orderList.clear(); // Clear previous orders
                Log.d(TAG, "Orders data changed, processing new data...");

                // Loop through all user snapshots
                for (DataSnapshot userSnapshot : dataSnapshot.getChildren()) {
                    String userId = userSnapshot.getKey(); // Get the user ID

                    // Check if the user ID matches the current user
                    if (userId.equals(currentUserId)) {
                        // Loop through the orders of the current user
                        for (DataSnapshot orderSnapshot : userSnapshot.getChildren()) {
                            Order order = orderSnapshot.getValue(Order.class);
                            if (order != null) {
                                orderList.add(order); // Add the order to the list
                                Log.d(TAG, "Order added: " + order); // Log order details
                            } else {
                                Log.d(TAG, "Order is null for snapshot: " + orderSnapshot);
                            }
                        }
                    }
                }

                Log.d(TAG, "Total Orders loaded: " + orderList.size()); // Log total number of orders
                adapter.notifyDataSetChanged(); // Notify the adapter about data changes
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e(TAG, "Error loading orders: " + databaseError.getMessage()); // Log error message
            }
        });
    }
}
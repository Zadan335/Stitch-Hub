package com.example.stitchhub;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;


public class homet extends Fragment {

    private BottomNavigationView bottomNavigationView;
    private DrawerLayout drawerLayout;
    private NavigationView navigationView;
    private ActionBarDrawerToggle drawerToggle;


    // Views for the tailor's profile card and dashboard stats
    private ImageView profileImageView;
    private TextView greetingTextView, emailTextView, totalEarningsTextView, activeOrdersTextView, orderRequestsTextView, completedOrdersTextView;

    private DatabaseReference ordersRef;
    private FirebaseUser currentUser;
    private double totalEarnings = 0;
    private int activeOrders = 0;
    private int orderRequests = 0;
    private int completedOrders = 0;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_homet, container, false);

        // Initialize views for profile and stats
        profileImageView = view.findViewById(R.id.tailorProfileImage);
        greetingTextView = view.findViewById(R.id.tailorGreeting);
        emailTextView = view.findViewById(R.id.tailorEmail);
        totalEarningsTextView = view.findViewById(R.id.textTotalEarnings);
        activeOrdersTextView = view.findViewById(R.id.textActiveOrders);
        orderRequestsTextView = view.findViewById(R.id.textOrderRequests);
        completedOrdersTextView = view.findViewById(R.id.textCompletedOrders);

        // Initialize Firebase
        currentUser = FirebaseAuth.getInstance().getCurrentUser();
        ordersRef = FirebaseDatabase.getInstance().getReference("orders");
// Initialize DrawerLayout and NavigationView
        drawerLayout = view.findViewById(R.id.drawer_layout);
        navigationView = view.findViewById(R.id.navigation_view);

        // Set up drawer toggle
        drawerToggle = new ActionBarDrawerToggle(getActivity(), drawerLayout, R.string.drawer_open, R.string.drawer_close);
        // Set up tailor's profile
        if (currentUser != null) {
            emailTextView.setText(currentUser.getEmail());

            // Load profile picture from Firebase Storage
            loadProfilePicture(currentUser.getUid());
        }

        // Fetch order statistics from Firebase
        fetchOrderStatistics();

        // Set up bottom navigation
        bottomNavigationView = view.findViewById(R.id.bottom_navigation);

        // Set up bottom navigation item selected listener
        bottomNavigationView.setOnNavigationItemSelectedListener(item -> {
            Fragment selectedFragment = null;
            int itemId = item.getItemId();

            // Log the item ID to check which item is selected
            Log.d("BottomNavigation", "Selected Item ID: " + itemId);

            if (itemId == R.id.nav_catalog) {
                selectedFragment = new CatalogFragment();

            } else if (itemId == R.id.nav_inbox) {
                selectedFragment = new TailorInboxFragment();
            } else if (itemId == R.id.nav_order) {
                selectedFragment = new tailororder();
            } else if (itemId == R.id.nav_home) {
                selectedFragment = new homet();
                return true; // Prevents reloading the same fragment
            }else if (itemId == R.id.nav_Custorder) {
                selectedFragment = new tailororder();
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
        navigationView.setNavigationItemSelectedListener(item -> {
            Fragment selectedFragment = null;
            int itemId = item.getItemId();

            // Log the item ID to check which item is selected
            Log.d("BottomNavigation", "Selected Item ID: " + itemId);

            if (itemId == R.id.nav_logout) {
                selectedFragment = new cust_or_tailor();

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


    // Helper method to load the selected fragment into the fragment container
    private void loadFragment(Fragment fragment) {

        getActivity().getSupportFragmentManager().beginTransaction()
                .setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out) // Set fade-in and fade-out animation
                .replace(R.id.fragment_container, fragment)
                .addToBackStack(null)
                .commit();
        }


    private void fetchOrderStatistics() {
        if (currentUser == null) return;

        String tailorUid = currentUser.getUid();
        Log.d("homet", "Current User UID: " + tailorUid); // Log the UID for debugging

        // Fetch orders for the current tailor
        ordersRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                // Log the number of orders retrieved
                Log.d("homet", "Number of customers: " + dataSnapshot.getChildrenCount());

                // Reset stats for fresh calculation
                totalEarnings = 0;
                activeOrders = 0;
                orderRequests = 0;
                completedOrders = 0;

                // Iterate through each customer in the database
                for (DataSnapshot customerSnapshot : dataSnapshot.getChildren()) {
                    // Iterate through orders under each customer
                    for (DataSnapshot orderSnapshot : customerSnapshot.getChildren()) {
                        Order currentOrder = orderSnapshot.getValue(Order.class);
                        Log.d("homet", "Retrieved order: " + currentOrder); // Log the order data

                        // Check if the order matches the tailorUid
                        if (currentOrder != null && currentOrder.getTailorUid().equals(tailorUid)) {
                            // Increment counts based on order status
                            switch (currentOrder.getOrderStatus()) {
                                case PENDING:
                                    orderRequests++;
                                    break;
                                case ACCEPTED:
                                    activeOrders++;
                                    break;
                                case COMPLETED:
                                    completedOrders++;
                                    totalEarnings += currentOrder.getTotalPrice(); // Add to total earnings
                                    break;
                            }
                        }
                    }
                }

                // Update the UI with fetched data
                totalEarningsTextView.setText("Earnings: PKR " + totalEarnings);
                activeOrdersTextView.setText("Active Orders: " + activeOrders);
                orderRequestsTextView.setText("Order Requests: " + orderRequests);
                completedOrdersTextView.setText("Completed Orders: " + completedOrders);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Handle error
                Log.e("homet", "Failed to fetch orders: " + databaseError.getMessage());
            }
        });
    }
    
    private void loadProfilePicture(String uid) {
        // Load tailor's profile picture from Firebase Storage
        // StorageReference profileRef = FirebaseStorage.getInstance().getReference().child("profilePictureUrl" + uid + ".jpg");
        // profileRef.getDownloadUrl().addOnSuccessListener(uri -> {
        // Load the image using an image loading library like Glide or Picasso
        // Glide.with(this).load(uri).into(profileImageView);
        // }).addOnFailureListener(e -> {
        // Handle the error, e.g., show a placeholder image
        // profileImageView.setImageResource(R.drawable.user);
        // });
    }
}
package com.example.stitchhub;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

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


public class tailororder extends Fragment {
    private RecyclerView orderRecyclerView;
    private OrderAdapter orderAdapter;
    private List<Order> orderList;
    private FirebaseAuth auth;
    private DatabaseReference orderRef;
    private String tailorUid;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_tailororder, container, false);

        orderRecyclerView = view.findViewById(R.id.recyclerViewOrders);
        orderRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        orderList = new ArrayList<>();
        orderAdapter = new OrderAdapter(orderList);
        orderRecyclerView.setAdapter(orderAdapter);

        // Initialize FirebaseAuth
        auth = FirebaseAuth.getInstance();
        tailorUid = auth.getCurrentUser().getUid(); // Get current tailor UID
        Log.d("TailorOrder", "Current Tailor UID: " + tailorUid); // Log for debugging

        // Get Firebase Database reference
        orderRef = FirebaseDatabase.getInstance().getReference("orders");

        // Fetch orders where tailorUid matches the current tailor's UID
        fetchOrders();
        BottomNavigationView bottomNavigationView=view.findViewById(R.id.bottom_navigation);
        bottomNavigationView.setOnNavigationItemSelectedListener(item -> {
            Fragment selectedFragment = null;
            int itemId = item.getItemId();

            // Log the item ID to check which item is selected
            Log.d("BottomNavigation", "Selected Item ID: " + itemId);

            if (itemId == R.id.nav_catalog) {
                selectedFragment = new CatalogFragment();
            }  else if (itemId == R.id.nav_home) {
                selectedFragment = new homet();
            } else if (itemId == R.id.nav_inbox) {
                selectedFragment = new TailorInboxFragment();
            } else if (itemId == R.id.nav_order) {
                selectedFragment = new tailororder();
                return true; // Prevents reloading the same fragment
            }
            else if (itemId == R.id.nav_Custorder) {
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
        return view;
    }
    private void loadFragment(Fragment fragment) {
        // Load the selected fragment into the container
        getActivity().getSupportFragmentManager().beginTransaction()
                .setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out) // Set fade-in and fade-out animation
                .replace(R.id.fragment_container, fragment)
                .addToBackStack(null)
                .commit();
    }
    private void fetchOrders() {
        // Get the reference to the orders node
        DatabaseReference ordersRef = FirebaseDatabase.getInstance().getReference("orders");

        // Listen for changes in the orders node
        ordersRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                orderList.clear(); // Clear the previous list

                // Iterate through each customer ID
                for (DataSnapshot customerSnapshot : dataSnapshot.getChildren()) {
                    // Iterate through each order under that customer
                    for (DataSnapshot orderSnapshot : customerSnapshot.getChildren()) {
                        Order order = orderSnapshot.getValue(Order.class);
                        if (order != null && order.getTailorUid() != null) {
                            if (order.getTailorUid().equals(tailorUid)) {
                                orderList.add(order); // Add the order to the list if the tailorUid matches
                                Log.d("TailorOrder", "Order found: " + order.toString());
                            }
                        }
                    }
                }
                Log.d("TailorOrder", "Total orders retrieved: " + orderList.size());
                orderAdapter.notifyDataSetChanged(); // Notify adapter about data changes
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.e("TailorOrder", "Database error: " + databaseError.getMessage());
            }
        });
    }
}
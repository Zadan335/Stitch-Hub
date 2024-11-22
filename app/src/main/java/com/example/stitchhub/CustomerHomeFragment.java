package com.example.stitchhub;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.core.app.ActivityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;


public class CustomerHomeFragment extends Fragment {

    private RecyclerView recyclerViewTailors;
    private TailorAdapter tailorAdapter;
    private List<Tailor> tailorList;
    private DatabaseReference databaseReference;
    private ImageView imageViewUserProfile, imageViewSlider; // Add ImageView for slider
    private TextView textViewGreeting;
    private Location currentUserLocation; // To hold the user's current location
    private static final double MAX_DISTANCE_KM = 7.0;
    private FusedLocationProviderClient fusedLocationProviderClient;
    private FirebaseAuth firebaseAuth;
    private FirebaseUser currentUser;
    private DrawerLayout drawerLayout;
    private NavigationView navigationView;
    private ActionBarDrawerToggle drawerToggle;
    private int[] imageList; // Array to hold the image resources for the slideshow
    private int currentImageIndex = 0; // Index to track the current image
    private Handler handler;
    private Runnable imageSwitcherRunnable;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_customer_home, container, false);

        imageViewUserProfile = view.findViewById(R.id.imageViewUserProfile);
        textViewGreeting = view.findViewById(R.id.textViewGreeting);
        imageViewSlider = view.findViewById(R.id.imageViewSlider); // Find the ImageView for the slider
        recyclerViewTailors = view.findViewById(R.id.recyclerViewTailors);

        // Setup RecyclerView
        recyclerViewTailors.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
        tailorList = new ArrayList<>();
        tailorAdapter = new TailorAdapter(getContext(), tailorList, this::onTailorSelected);
        recyclerViewTailors.setAdapter(tailorAdapter);

        // Initialize Firebase Authentication
        firebaseAuth = FirebaseAuth.getInstance();
        currentUser = firebaseAuth.getCurrentUser();

        // Load current user's profile and name
        loadUserProfile();

        // Initialize location services
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(getActivity());
        getLastKnownLocation();

        // Setup image slideshow
        setupImageSlideshow();

        BottomNavigationView bottomNavigationView = view.findViewById(R.id.bottomNavigationView);
navigationView=view.findViewById(R.id.navigation_view);
        // Set up bottom navigation item selected listener
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
                navigateToFragment(selectedFragment);
            } else {
                Log.d("BottomNavigation", "No Fragment Selected");
            }

            return true;
        });
        bottomNavigationView.setOnNavigationItemSelectedListener(item -> {
            Fragment selectedFragment = null;
            int itemId = item.getItemId();

            if (itemId == R.id.navigation_inbox) {
                selectedFragment = new UserInboxFragment();
            } else if (itemId == R.id.navigation_explore_tailors) {
                selectedFragment = new Explore();
            } else if (itemId == R.id.navigation_home) {
                return true;
            }else if (itemId == R.id.navigation_my_orders)
                selectedFragment = new orders();
            if (selectedFragment != null) {
                navigateToFragment(selectedFragment);
            }

            return true;
        });

        return view;
    }

    private void navigateToFragment(Fragment fragment) {
        getActivity().getSupportFragmentManager().beginTransaction()
                .setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out) // Set fade-in and fade-out animation
                .replace(R.id.fragment_container, fragment)
                .addToBackStack(null)
                .commit();
    }

    private void loadUserProfile() {
        if (currentUser != null) {
            String userId = currentUser.getUid();
            databaseReference = FirebaseDatabase.getInstance().getReference("users").child(userId);

            databaseReference.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()) {
                        String userName = dataSnapshot.child("name").getValue(String.class);
                        if (userName != null) {
                            textViewGreeting.setText("HI " + userName);
                        }
                        String profileImageUrl = dataSnapshot.child("profileImageUrl").getValue(String.class);
                        if (profileImageUrl != null) {
                            Glide.with(getContext()).load(profileImageUrl).into(imageViewUserProfile);
                        } else {
                            imageViewUserProfile.setImageResource(R.drawable.user);
                        }
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    Toast.makeText(getContext(), "Failed to load user profile", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    private void getLastKnownLocation() {
        if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
            return;
        }

        fusedLocationProviderClient.getLastLocation().addOnCompleteListener(new OnCompleteListener<Location>() {
            @Override
            public void onComplete(@NonNull Task<Location> task) {
                if (task.isSuccessful() && task.getResult() != null) {
                    currentUserLocation = task.getResult();
                    loadTailors();
                } else {
                    Log.e("CustomerHomeFragment", "Failed to get current location");
                }
            }
        });
    }

    private void loadTailors() {
        databaseReference = FirebaseDatabase.getInstance().getReference("tailors");

        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                tailorList.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Tailor tailor = snapshot.getValue(Tailor.class);
                    if (tailor != null && currentUserLocation != null) {
                        Double latitude = tailor.getLatitude();
                        Double longitude = tailor.getLongitude();

                        if (latitude != null && longitude != null) {
                            Location tailorLocation = new Location("provider");
                            tailorLocation.setLatitude(latitude);
                            tailorLocation.setLongitude(longitude);

                            float distanceInMeters = currentUserLocation.distanceTo(tailorLocation);
                            double distanceInKm = distanceInMeters / 1000.0;

                            if (distanceInKm <= MAX_DISTANCE_KM) {
                                String tailorUid = snapshot.getKey();
                                tailor.setUid(tailorUid);
                                tailorList.add(tailor);
                            }
                        }
                    }
                }
                tailorAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(getContext(), "Failed to load tailors", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void setupImageSlideshow() {
        // Define an array of image resources for the slideshow
        imageList = new int[]{R.drawable.s1, R.drawable.s2, R.drawable.s3, R.drawable.s4};

        // Initialize the handler and the runnable
        handler = new Handler();
        imageSwitcherRunnable = new Runnable() {
            @Override
            public void run() {
                currentImageIndex = (currentImageIndex + 1) % imageList.length; // Cycle through the images
                imageViewSlider.setImageResource(imageList[currentImageIndex]); // Set the next image
                handler.postDelayed(this, 3000); // Change the image every 3 seconds
            }
        };

        // Start the slideshow
        handler.post(imageSwitcherRunnable);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        // Stop the handler when the view is destroyed to avoid memory leaks
        if (handler != null) {
            handler.removeCallbacks(imageSwitcherRunnable);
        }
    }

    private void onTailorSelected(Tailor tailor) {
        openPlaceOrderFragment(tailor.getUid());
    }

    private void openPlaceOrderFragment(String tailorUid) {
        PlaceOrderFragment placeOrderFragment = new PlaceOrderFragment();
        Bundle args = new Bundle();
        args.putString("TAILOR_UID", tailorUid);
        placeOrderFragment.setArguments(args);
        navigateToFragment(placeOrderFragment);
    }
}
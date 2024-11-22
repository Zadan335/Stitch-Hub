package com.example.stitchhub;

import android.Manifest;
import android.app.AlertDialog;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.res.ResourcesCompat;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.osmdroid.api.IMapController;
import org.osmdroid.config.Configuration;
import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.Marker;

import java.util.ArrayList;
import java.util.List;

public class Explore extends Fragment {

    private MapView mapView;
    private FusedLocationProviderClient fusedLocationClient;
    private List<GeoPoint> tailorLocations = new ArrayList<>();
    BottomNavigationView bottomNavigationView;
    private static final int LOCATION_REQUEST_CODE = 1001;
    private double currentLat, currentLon;
    private static final double RADIUS_KM = 7.0;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_explore, container, false);

        // Initialize osmdroid with the application context
        Configuration.getInstance().load(requireContext(), PreferenceManager.getDefaultSharedPreferences(requireContext()));

        // Initialize MapView
        mapView = view.findViewById(R.id.mapView);
        mapView.setTileSource(TileSourceFactory.MAPNIK);
        mapView.setBuiltInZoomControls(true);
        mapView.setMultiTouchControls(true);
        bottomNavigationView = view.findViewById(R.id.bottom_navigation);
        setupBottomNavigation();
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireContext());

        // Check for location permission and fetch location
        if (checkPermission()) {
            getCurrentLocation();
        } else {
            requestLocationPermission();
        }

        return view;
    }

    // Check if the location permission is granted
    private boolean checkPermission() {
        return ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED;
    }

    // Request location permission
    private void requestLocationPermission() {
        ActivityCompat.requestPermissions(requireActivity(), new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_REQUEST_CODE);
    }

    // Fetch current location using FusedLocationProviderClient
    private void getCurrentLocation() {
        if (checkPermission()) {
            fusedLocationClient.getLastLocation().addOnSuccessListener(requireActivity(), location -> {
                if (location != null) {
                    currentLat = location.getLatitude();
                    currentLon = location.getLongitude();
                    GeoPoint currentLocation = new GeoPoint(currentLat, currentLon);

                    // Center map on current location
                    IMapController mapController = mapView.getController();
                    mapController.setZoom(15.0);
                    mapController.setCenter(currentLocation);

                    // Add marker for current location with a different style
                    addCustomMarker(currentLocation, "You are here", R.drawable.user_icon);

                    // Fetch nearby tailors
                    fetchNearbyTailors(currentLat, currentLon);
                } else {
                    Toast.makeText(requireContext(), "Unable to get location", Toast.LENGTH_SHORT).show();
                }
            });
        }
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
            } else if (itemId == R.id.navigation_inbox) {
                selectedFragment = new UserInboxFragment();

                Log.d("InboxFragment", "Orders selected");

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
    // Fetch tailors within a radius from Firebase
    private void fetchNearbyTailors(double lat, double lon) {
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("tailors");
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Double tailorLat = snapshot.child("latitude").getValue(Double.class);
                    Double tailorLon = snapshot.child("longitude").getValue(Double.class);
                    String tailorEmail = snapshot.child("email").getValue(String.class);
                    String tailorImageUrl = snapshot.child("profilePictureUrl").getValue(String.class);
                    String tailorUid = snapshot.getKey(); // Get the UID of the tailor

                    if (tailorLat != null && tailorLon != null) {
                        // Check if the tailor is within the radius
                        if (distanceBetween(currentLat, currentLon, tailorLat, tailorLon) <= RADIUS_KM) {
                            GeoPoint tailorLocation = new GeoPoint(tailorLat, tailorLon);
                            addTailorMarker(tailorLocation, tailorEmail, tailorUid, tailorImageUrl);  // Add tailor marker with UID, email, and image
                        }
                    } else {
                        Log.e("ExploreFragment", "Tailor location data missing for: " + tailorEmail);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Handle errors
            }
        });
    }

    // Add tailor marker to map
    private void addTailorMarker(GeoPoint location, String email, String tailorUid, String imageUrl) {
        Marker marker = new Marker(mapView);
        marker.setPosition(location);
        marker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM);
        marker.setTitle(email);
        mapView.getOverlays().add(marker);

        // Set marker click listener to show tailor details dialog
        marker.setOnMarkerClickListener((marker1, mapView1) -> {
            showTailorDetailsDialog(tailorUid, email, imageUrl);  // Show dialog with tailor details
            return true;
        });
    }

    // Add custom marker to map (for user location or other special points)
    private void addCustomMarker(GeoPoint location, String title, int drawableResource) {
        Marker marker = new Marker(mapView);
        marker.setPosition(location);
        marker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM);
        marker.setTitle(title);
        marker.setIcon(ResourcesCompat.getDrawable(getResources(), drawableResource, null));  // Use a custom icon
        mapView.getOverlays().add(marker);
    }

    // Calculate the distance between two lat/lon pairs
    private double distanceBetween(double lat1, double lon1, double lat2, double lon2) {
        float[] result = new float[1];
        Location.distanceBetween(lat1, lon1, lat2, lon2, result);
        return result[0] / 1000; // Convert to kilometers
    }

    @Override
    public void onResume() {
        super.onResume();
        mapView.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        mapView.onPause();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mapView.onDetach();
    }

    // Handle location permission result
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == LOCATION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                getCurrentLocation();
            } else {
                Toast.makeText(requireContext(), "Location permission denied", Toast.LENGTH_SHORT).show();
            }
        }
    }

    // Show tailor details in a dialog
    private void showTailorDetailsDialog(String tailorUid, String email, String imageUrl) {
        // Create a dialog to show tailor's email and picture
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        LayoutInflater inflater = requireActivity().getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialog_tailor_details, null);

        // Find and set the views in the dialog (email and picture)
        TextView emailTextView = dialogView.findViewById(R.id.emailTextView);
        ImageView imageView = dialogView.findViewById(R.id.tailorImageView);
        emailTextView.setText(email);

        // Load tailor's image using an image loader (e.g., Glide or Picasso)
        Glide.with(requireContext()).load(imageUrl).into(imageView);

        builder.setView(dialogView)
                .setPositiveButton("Close", (dialog, id) -> dialog.dismiss());

        // Show dialog
        AlertDialog dialog = builder.create();
        dialog.show();

        // Handle click on email and picture
        emailTextView.setOnClickListener(v -> {
            navigateToPlaceOrderFragment(tailorUid);
            dialog.dismiss();
        });

        imageView.setOnClickListener(v -> {
            navigateToPlaceOrderFragment(tailorUid);
            dialog.dismiss();
        });
    }

    // Navigate to PlaceOrderFragment
    private void navigateToPlaceOrderFragment(String tailorUid) {
        PlaceOrderFragment placeOrderFragment = new PlaceOrderFragment();

        // Log the Tailor UID for debugging purposes
        Log.d("ExploreFragment", "Tailor UID: " + tailorUid);

        // Pass tailor UID to the PlaceOrderFragment using a Bundle
        Bundle args = new Bundle();
        args.putString("TAILOR_UID", tailorUid);
        placeOrderFragment.setArguments(args);

        // Replace the current fragment with PlaceOrderFragment
        navigateToFragment(placeOrderFragment);
    }

    // Method to handle fragment transactions
    private void navigateToFragment(Fragment fragment) {
        getActivity().getSupportFragmentManager().beginTransaction()
                .setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out) // Set fade-in and fade-out animation
                .replace(R.id.fragment_container, fragment)
                .addToBackStack(null)
                .commit();
    }
}
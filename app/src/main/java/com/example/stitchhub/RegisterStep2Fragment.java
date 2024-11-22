package com.example.stitchhub;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.material.textfield.TextInputEditText;


public class RegisterStep2Fragment extends Fragment {

    private TextInputEditText editTextAddress, editTextLatitude, editTextLongitude;
    private Button buttonNext, buttonGetLocation;
    private FusedLocationProviderClient fusedLocationClient;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_register_step2, container, false);

        // Initialize the EditTexts and Buttons
        editTextAddress = view.findViewById(R.id.editTextAddress);
        editTextLatitude = view.findViewById(R.id.editTextLatitude);
        editTextLongitude = view.findViewById(R.id.editTextLongitude);
        buttonNext = view.findViewById(R.id.buttonNext);
        buttonGetLocation = view.findViewById(R.id.buttonGetCurrentLocation);

        // Initialize FusedLocationProviderClient
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireActivity());

        // Set OnClickListener for the Get Location button
        buttonGetLocation.setOnClickListener(v -> getCurrentLocation());

        // Set OnClickListener for the Next button
        buttonNext.setOnClickListener(v -> {
            String address = editTextAddress.getText().toString().trim();
            String latitudeStr = editTextLatitude.getText().toString().trim();
            String longitudeStr = editTextLongitude.getText().toString().trim();

            // Validate Address: Should not be empty and should contain at least one letter (not only numbers)
            if (!address.matches(".*[a-zA-Z]+.*")) {
                Toast.makeText(getActivity(), "Address should contain letters and cannot be only numbers", Toast.LENGTH_SHORT).show();
                return;
            }

            // Validate Latitude and Longitude (must be numbers)
            if (latitudeStr.isEmpty() || longitudeStr.isEmpty()) {
                Toast.makeText(getActivity(), "Please fill in all fields", Toast.LENGTH_SHORT).show();
                return;
            }

            Double latitude = null;
            Double longitude = null;
            try {
                latitude = Double.parseDouble(latitudeStr);
                longitude = Double.parseDouble(longitudeStr);
            } catch (NumberFormatException e) {
                Toast.makeText(getActivity(), "Invalid latitude or longitude format", Toast.LENGTH_SHORT).show();
                return;
            }

            // Bundle the data to pass to the next fragment
            Bundle bundle = new Bundle();
            bundle.putString("address", address);
            bundle.putDouble("latitude", latitude);
            bundle.putDouble("longitude", longitude);

            // Proceed to the next registration step with animations
            RegisterStep3Fragment registerStep3Fragment = new RegisterStep3Fragment();
            registerStep3Fragment.setArguments(bundle);

            getParentFragmentManager().beginTransaction()
                    .setCustomAnimations(R.anim.slide_in_right, R.anim.slide_out_left)
                    .replace(R.id.fragment_container, registerStep3Fragment)
                    .addToBackStack(null)
                    .commit();
        });

        return view;
    }

    // Method to get the current location
    private void getCurrentLocation() {
        // Get last known location
        if (ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // Request the necessary permission
            ActivityCompat.requestPermissions(requireActivity(), new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
            return;
        }
        fusedLocationClient.getLastLocation()
                .addOnSuccessListener(requireActivity(), location -> {
                    if (location != null) {
                        // Autofill latitude and longitude fields
                        editTextLatitude.setText(String.valueOf(location.getLatitude()));
                        editTextLongitude.setText(String.valueOf(location.getLongitude()));
                    } else {
                        Toast.makeText(getActivity(), "Unable to get current location", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == 1) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission was granted, proceed with getting location
                getCurrentLocation();
            } else {
                // Permission was denied, show a message to the user
                Toast.makeText(getContext(), "Location permission is required to get the current location", Toast.LENGTH_SHORT).show();
            }
        }
    }
}
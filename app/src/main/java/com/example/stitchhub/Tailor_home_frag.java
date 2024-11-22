package com.example.stitchhub;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;

import com.google.android.material.bottomnavigation.BottomNavigationView;

public class Tailor_home_frag extends Fragment {

    private BottomNavigationView bottomNavigationView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_tailor_home_frag, container, false);

        bottomNavigationView = view.findViewById(R.id.bottomNavigationView);

        // Load the default fragment (Home) when the fragment is created
        loadFragment(new Tailor_home_frag()); // Replace with your default fragment

        // Set up the bottom navigation item selected listener
        bottomNavigationView.setOnNavigationItemSelectedListener(item -> {
            Fragment selectedFragment = null;
            int itemId = item.getItemId();

            // Use if-else instead of switch
            if (itemId == R.id.nav_home) {
                 selectedFragment = new Tailor_home_frag(); // Your default fragment
            } else if (itemId == R.id.nav_catalog) {
                 selectedFragment = new CatalogFragment(); // Your catalog fragment
            // Your portfolio fragment
            } else if (itemId == R.id.nav_inbox) {
                  selectedFragment = new ChatInboxFragment(); // Your dashboard fragment
            }else if (itemId == R.id.nav_Custorder) {
                selectedFragment = new TailorOrderFragment();
                return true; // Prevents reloading the same fragment
            }

            if (selectedFragment != null) {
                loadFragment(selectedFragment);
            }

            return true;
        });

        return view;
    }

    // Helper method to load the selected fragment
    private void loadFragment(Fragment fragment) {
        getChildFragmentManager()
                .beginTransaction()
                .replace(R.id.fragmentContainer, fragment) // Ensure this ID is correct
                .commit();
    }
}
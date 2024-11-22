package com.example.stitchhub;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

import com.google.android.material.textfield.TextInputEditText;



public class RegisterTailorStep1Fragment extends Fragment {

    private TextInputEditText editTextName, editTextPhone;
    private Button buttonNext;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_register_step1, container, false);

        editTextName = view.findViewById(R.id.editTextName);
        editTextPhone = view.findViewById(R.id.editTextPhone);
        buttonNext = view.findViewById(R.id.buttonNext);

        buttonNext.setOnClickListener(v -> {
            String name = editTextName.getText().toString().trim();
            String phone = editTextPhone.getText().toString().trim();

            // Validate name (characters only)
            if (!name.matches("[a-zA-Z\\s]+")) {
                Toast.makeText(getActivity(), "Name should contain only letters", Toast.LENGTH_SHORT).show();
                return;
            }

            // Validate phone number (11 digits)
            if (phone.length() != 11 || !phone.matches("\\d+")) {
                Toast.makeText(getActivity(), "Phone number must be 11 digits", Toast.LENGTH_SHORT).show();
                return;
            }

            if (TextUtils.isEmpty(name) || TextUtils.isEmpty(phone)) {
                Toast.makeText(getActivity(), "Please fill out all fields", Toast.LENGTH_SHORT).show();
            } else {
                // Store data in a Bundle
                Bundle bundle = new Bundle();
                bundle.putString("name", name);
                bundle.putString("phone", phone);

                // Navigate to the next step using FragmentTransaction with animations
                RegisterStep2Fragment step2Fragment = new RegisterStep2Fragment();
                step2Fragment.setArguments(bundle);

                getFragmentManager().beginTransaction()
                        .setCustomAnimations(R.anim.slide_in_right, R.anim.slide_out_left) // Set animations
                        .replace(R.id.fragment_container, step2Fragment)
                        .addToBackStack(null)
                        .commit();
            }
        });

        return view;
    }
}
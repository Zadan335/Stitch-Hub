package com.example.stitchhub;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.google.android.material.textfield.TextInputEditText;


public class Register extends Fragment {
    private TextInputEditText editTextName, editTextPhone;
    private Button buttonNext;

    public Register() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_register, container, false);

        editTextName = view.findViewById(R.id.editTextName);
        editTextPhone = view.findViewById(R.id.editTextPhone);
        buttonNext = view.findViewById(R.id.buttonNext);

        buttonNext.setOnClickListener(v -> {
            // Validate input
            if (validateInputs()) {
                // Pass data to the second fragment
                Bundle bundle = new Bundle();
                bundle.putString("name", editTextName.getText().toString().trim());
                bundle.putString("phone", editTextPhone.getText().toString().trim());

                register2 fragment2 = new register2();
                fragment2.setArguments(bundle);

                FragmentManager fragmentManager = getFragmentManager();
                if (fragmentManager != null) {
                    FragmentTransaction transaction = fragmentManager.beginTransaction();

                    // Set custom animations for the transition
                    transaction.setCustomAnimations(R.anim.slide_in_right, R.anim.slide_out_left);

                    transaction.replace(R.id.fragment_container, fragment2); // Ensure container ID matches your activity's layout
                    transaction.addToBackStack(null);
                    transaction.commit();
                }
            }
        });

        return view;
    }
    private boolean validateInputs() {
        // Add validation logic here
        String name = editTextName.getText().toString().trim();
        String phone = editTextPhone.getText().toString().trim();

        if (TextUtils.isEmpty(name)) {
            editTextName.setError("Name is required");
            return false;
        }

        if (TextUtils.isEmpty(phone)) {
            editTextPhone.setError("Phone number is required");
            return false;
        }

        return true;
    }
}

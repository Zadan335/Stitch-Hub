package com.example.stitchhub;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;


public class register2 extends Fragment {

    private TextInputEditText editTextEmail, editTextPassword, editTextConfirmPassword;
    private Button buttonFinish;

    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase;

    public register2() {
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference("users");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_register2, container, false);

        editTextEmail = view.findViewById(R.id.editTextEmail);
        editTextPassword = view.findViewById(R.id.editTextPassword);
        editTextConfirmPassword = view.findViewById(R.id.editTextConfirmPassword); // Added confirm password field
        buttonFinish = view.findViewById(R.id.buttonFinish);

        buttonFinish.setOnClickListener(v -> registerUser());

        return view;
    }

    private void registerUser() {
        String email = editTextEmail.getText().toString().trim();
        String password = editTextPassword.getText().toString().trim();
        String confirmPassword = editTextConfirmPassword.getText().toString().trim();

        if (validateInputs(email, password, confirmPassword)) {
            mAuth.createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            FirebaseUser user = mAuth.getCurrentUser();
                            if (user != null) {
                                String userId = user.getUid();
                                // Get data from the first fragment
                                Bundle bundle = getArguments();
                                if (bundle != null) {
                                    String name = bundle.getString("name");
                                    String phone = bundle.getString("phone");
                                    saveUserData(userId, name, email, phone);
                                }
                            }
                            Toast.makeText(getContext(), "Registration Successful", Toast.LENGTH_SHORT).show();
                            navigateToLoginFragment();
                        } else {
                            Toast.makeText(getContext(), "Registration failed: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
        }
    }

    private boolean validateInputs(String email, String password, String confirmPassword) {
        if (!isValidEmail(email)) {
            Toast.makeText(getContext(), "Invalid email format", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (password.length() < 6) {
            Toast.makeText(getContext(), "Password must be at least 6 characters long", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (!password.equals(confirmPassword)) {
            Toast.makeText(getContext(), "Passwords do not match", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    private boolean isValidEmail(String email) {
        // Check for multiple occurrences of ".com"
        String[] parts = email.split("@");
        if (parts.length != 2) {
            return false;
        }

        String localPart = parts[0];
        String domainPart = parts[1];

        // Ensure the domain part does not contain numeric values
        if (domainPart.matches(".*\\d.*")) {
            return false;
        }

        // Ensure no more than one occurrence of ".com"
        if (domainPart.contains(".com") && domainPart.indexOf(".com") != domainPart.lastIndexOf(".com")) {
            return false;
        }

        // Ensure there are no consecutive ".com" or invalid placements
        if (email.contains(".com.com") || email.contains(".@")) {
            return false;
        }

        return true;
    }

    private void saveUserData(String userId, String name, String email, String phone) {
        User user = new User(name, email, phone);
        mDatabase.child(userId).setValue(user)
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(getContext(), "User data saved successfully", Toast.LENGTH_SHORT).show();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(getContext(), "Failed to save user data: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }

    private void navigateToLoginFragment() {
        Fragment login = new login();
        FragmentManager fragmentManager = getFragmentManager();
        if (fragmentManager != null) {
            fragmentManager.beginTransaction()
                    .replace(R.id.fragment_container, login) // Ensure container ID matches your activity's layout
                    .addToBackStack(null)
                    .commit();
        }
    }
}
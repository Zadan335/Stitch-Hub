package com.example.stitchhub;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.crashlytics.buildtools.reloc.org.apache.http.util.TextUtils;


public class Logintailor extends Fragment {

    private TextInputEditText editTextEmail, editTextPassword;
    private TextInputLayout emailLayout, passwordLayout;
    private Button buttonLogin, buttonRegister;
    private FirebaseAuth mAuth;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_logintailor, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance();

        // Find views
        editTextEmail = view.findViewById(R.id.email);
        editTextPassword = view.findViewById(R.id.password);
        emailLayout = view.findViewById(R.id.emailLayout);
        passwordLayout = view.findViewById(R.id.passwordLayout);
        buttonLogin = view.findViewById(R.id.buttonLogin);
        buttonRegister = view.findViewById(R.id.buttonRegister);

        // Set up login button click listener
        buttonLogin.setOnClickListener(v -> loginUser());

        // Set up register button click listener
        buttonRegister.setOnClickListener(v -> onRegisterClick());
    }

    private void loginUser() {
        String email = editTextEmail.getText().toString().trim();
        String password = editTextPassword.getText().toString().trim();

        // Validate input
        if (email.isEmpty()) {
            emailLayout.setError("Email is required");
            return;
        } else {
            emailLayout.setError(null);
        }

        if (password.isEmpty()) {
            passwordLayout.setError("Password is required");
            return;
        } else {
            passwordLayout.setError(null);
        }

        // Sign in with Firebase Auth
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(requireActivity(), task -> {
                    if (task.isSuccessful()) {
                        // Login successful
                        Toast.makeText(requireActivity(), "Login successful", Toast.LENGTH_SHORT).show();

                        // Navigate to TailorHome fragment
                        Fragment homet = new homet(); // Make sure TailorHomeFragment is your actual fragment class
                        FragmentTransaction fragmentTransaction = requireActivity().getSupportFragmentManager().beginTransaction();
                        fragmentTransaction.replace(R.id.fragment_container, homet); // Ensure this ID matches the container in your activity
                        fragmentTransaction.addToBackStack(null);
                        fragmentTransaction.commit();

                    } else {
                        // Login failed
                        String errorMessage = task.getException() instanceof FirebaseAuthException
                                ? ((FirebaseAuthException) task.getException()).getErrorCode()
                                : task.getException().getMessage();
                        Toast.makeText(requireActivity(), "Login failed: " + errorMessage, Toast.LENGTH_SHORT).show();
                    }
                });
    }

    public void onRegisterClick() {
        Fragment registerTailorStep1Fragment = new RegisterTailorStep1Fragment();
        FragmentTransaction fragmentTransaction = requireActivity().getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.fragment_container, registerTailorStep1Fragment); // Ensure this ID matches the container in your activity
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();
    }
}
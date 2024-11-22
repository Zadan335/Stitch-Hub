package com.example.stitchhub;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.fragment.app.Fragment;

import com.google.android.material.textfield.TextInputEditText;


public class RegisterStep3Fragment extends Fragment {

    private TextInputEditText editTextEmail, editTextPassword, editTextConfirmPassword;
    private Button buttonNext;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_register_step3, container, false);

        editTextEmail = view.findViewById(R.id.editTextEmail);
        editTextPassword = view.findViewById(R.id.editTextPassword);
        editTextConfirmPassword = view.findViewById(R.id.editTextConfirmPassword);
        buttonNext = view.findViewById(R.id.buttonNext);

        // Set OnClickListener for the Next button
        buttonNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = editTextEmail.getText().toString().trim();
                String password = editTextPassword.getText().toString().trim();
                String confirmPassword = editTextConfirmPassword.getText().toString().trim();

                // Validate email format
                if (!isValidEmail(email)) {
                    editTextEmail.setError("Invalid email format");
                    editTextEmail.requestFocus();
                    return;
                }

                // Check if password and confirmation match
                if (!password.equals(confirmPassword)) {
                    editTextConfirmPassword.setError("Passwords do not match");
                    editTextConfirmPassword.requestFocus();
                    return;
                }

                // Bundle the data to pass it to the next fragment
                Bundle bundle = getArguments();
                if (bundle == null) {
                    bundle = new Bundle();
                }
                bundle.putString("email", email);
                bundle.putString("password", password);

                // Move to RegisterStep4Fragment with animations
                RegisterStep4Fragment registerStep4Fragment = new RegisterStep4Fragment();
                registerStep4Fragment.setArguments(bundle);

                getParentFragmentManager().beginTransaction()
                        .setCustomAnimations(R.anim.slide_in_right, R.anim.slide_out_left) // Set custom animations
                        .replace(R.id.fragment_container, registerStep4Fragment)
                        .addToBackStack(null)
                        .commit();
            }
        });

        return view;
    }

    // Method to validate email with additional checks
    private boolean isValidEmail(String email) {
        // Regex to check email format:
        // - Only one "@" allowed
        // - No numbers allowed after "@"
        // - ".com" should not be repeated
        String emailPattern = "^[a-zA-Z0-9._%+-]+@[a-zA-Z]+(\\.[a-zA-Z]+)+$";

        if (!email.matches(emailPattern)) {
            return false; // Invalid format
        }

        // Check that ".com" doesn't appear more than once
        if (email.contains(".com.com")) {
            return false;
        }

        // Ensure no numbers after "@"
        String domainPart = email.substring(email.indexOf("@") + 1);
        if (domainPart.matches(".*\\d.*")) {  // Checks if there is any digit in the domain part
            return false;
        }

        return true; // Valid email
    }
}
package com.example.stitchhub;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;


public class RegisterStep4Fragment extends Fragment {

    private static final int PICK_IMAGE_REQUEST = 100;
    private static final int MAX_RETRIES = 3;

    private ImageView imageViewProfile;
    private Button buttonUpload, buttonComplete;
    private Uri imageUri;
    private String email, password, address, name, phone;
    private Double latitude, longitude;
    private FirebaseAuth mAuth;
    private DatabaseReference databaseReference;
    private StorageReference storageReference;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_register_step4, container, false);

        imageViewProfile = view.findViewById(R.id.imageViewProfile);
        buttonUpload = view.findViewById(R.id.buttonUpload);
        buttonComplete = view.findViewById(R.id.buttonComplete);

        mAuth = FirebaseAuth.getInstance();
        databaseReference = FirebaseDatabase.getInstance().getReference("tailors");
        storageReference = FirebaseStorage.getInstance().getReference("profile_pictures");

        // Retrieve data from previous steps
        Bundle bundle = getArguments();
        if (bundle != null) {
            name = bundle.getString("name");
            phone = bundle.getString("phone");
            address = bundle.getString("address");
            latitude = bundle.getDouble("latitude", 0.0); // Default to 0.0 if not found
            longitude = bundle.getDouble("longitude", 0.0);
            email = bundle.getString("email");
            password = bundle.getString("password");
        } else {
            Log.e("RegisterStep4Fragment", "Bundle is null.");
        }

        buttonUpload.setOnClickListener(v -> openImagePicker());

        buttonComplete.setOnClickListener(v -> {
            if (imageUri != null) {
                uploadProfilePicture(imageUri);
            } else {
                Toast.makeText(getContext(), "Please upload a profile picture", Toast.LENGTH_SHORT).show();
            }
        });

        return view;
    }

    private void openImagePicker() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, PICK_IMAGE_REQUEST);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == Activity.RESULT_OK && data != null) {
            imageUri = data.getData();
            imageViewProfile.setImageURI(imageUri);
        }
    }

    private void uploadProfilePicture(final Uri uri) {
        if (uri != null) {
            final StorageReference fileRef = storageReference.child(mAuth.getCurrentUser().getUid() + ".jpg");

            fileRef.putFile(uri)
                    .addOnSuccessListener(taskSnapshot -> fileRef.getDownloadUrl()
                            .addOnSuccessListener(this::saveTailorData)
                            .addOnFailureListener(e -> handleFailure("Failed to retrieve download URL", e)))
                    .addOnFailureListener(e -> handleUploadFailure(uri, fileRef, 1));
        } else {
            Toast.makeText(getContext(), "No file selected", Toast.LENGTH_SHORT).show();
        }
    }

    private void handleUploadFailure(final Uri uri, final StorageReference fileRef, final int retryCount) {
        if (retryCount <= MAX_RETRIES) {
            Toast.makeText(getContext(), "Retrying upload... (" + retryCount + "/" + MAX_RETRIES + ")", Toast.LENGTH_SHORT).show();
            fileRef.putFile(uri)
                    .addOnSuccessListener(taskSnapshot -> fileRef.getDownloadUrl()
                            .addOnSuccessListener(this::saveTailorData)
                            .addOnFailureListener(e -> handleFailure("Failed to retrieve download URL on retry", e)))
                    .addOnFailureListener(e -> handleUploadFailure(uri, fileRef, retryCount + 1));
        } else {
            Toast.makeText(getContext(), "Failed to upload profile picture after multiple attempts", Toast.LENGTH_SHORT).show();
        }
    }

    private void handleFailure(String message, Exception e) {
        e.printStackTrace();
        Log.e("RegisterStep4Fragment", message + ": " + e.getMessage());
        Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();
    }

    private void saveTailorData(Uri profilePictureUri) {
        // Create a Tailor object with the uid field
        Tailor tailor = new Tailor(name, phone, address, latitude, longitude, email, profilePictureUri.toString(), mAuth.getCurrentUser().getUid());

        mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                databaseReference.child(mAuth.getCurrentUser().getUid()).setValue(tailor)
                        .addOnCompleteListener(task1 -> {
                            if (task1.isSuccessful()) {
                                Toast.makeText(getContext(), "Registration completed successfully", Toast.LENGTH_SHORT).show();

                                // Redirect to LoginTailorFragment
                                Fragment loginTailorFragment = new Logintailor();
                                getParentFragmentManager().beginTransaction()
                                        .setCustomAnimations(R.anim.fade_in2, R.anim.fade_out)
                                        .replace(R.id.fragment_container, loginTailorFragment)
                                        .addToBackStack(null)
                                        .commit();

                            } else {
                                handleFailure("Failed to save tailor data", task1.getException());
                            }
                        });
            } else {
                handleFailure("Failed to create user account", task.getException());
            }
        });
    }
}

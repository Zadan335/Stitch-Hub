package com.example.stitchhub;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.List;


public class CatalogFragment extends Fragment {

    private static final int PICK_IMAGE_REQUEST = 1;
    private EditText designCodeEditText;
    private EditText priceEditText;
    private RadioGroup genderRadioGroup;
    private Button uploadImageButton;
    private ImageView imageView;
    private Button addCatalogButton;
    private RecyclerView recyclerView;
    private CatalogsAdapter catalogsAdapter;
    private List<CatalogProduct> catalogProducts;

    private Uri imageUri;
    private FirebaseStorage firebaseStorage;
    private DatabaseReference catalogRef;
    private String userId;

    private BottomNavigationView bottomNavigationView;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        firebaseStorage = FirebaseStorage.getInstance();
        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();

        userId = firebaseAuth.getCurrentUser().getUid(); // Get the current user ID
        catalogRef = firebaseDatabase.getReference("catalog").child(userId); // Catalog reference for current user
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_catalog, container, false);

        // Initialize UI elements
        designCodeEditText = view.findViewById(R.id.designCodeEditText);
        priceEditText = view.findViewById(R.id.priceEditText);
        genderRadioGroup = view.findViewById(R.id.genderRadioGroup);
        uploadImageButton = view.findViewById(R.id.uploadImageButton);
        imageView = view.findViewById(R.id.imageView);
        addCatalogButton = view.findViewById(R.id.addCatalogButton);
        recyclerView = view.findViewById(R.id.catalogRecyclerView);
        bottomNavigationView = view.findViewById(R.id.bottom_navigation); // Initialize BottomNavigationView

        // Initialize RecyclerView
        catalogProducts = new ArrayList<>();
        catalogsAdapter = new CatalogsAdapter(catalogProducts, product -> {
            // Handle edit click
            // TODO: Implement edit functionality
        }, product -> {
            deleteCatalogProduct(product.getDesignCode());
            // TODO: Implement delete functionality
        });

        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(catalogsAdapter);

        uploadImageButton.setOnClickListener(v -> openImagePicker());
        addCatalogButton.setOnClickListener(v -> checkIfDesignCodeIsUniqueAndAddCatalog());

        // Load existing catalog products
        loadCatalogProducts();

        // Set up Bottom Navigation
        bottomNavigationView.setOnNavigationItemSelectedListener(item -> {
            Fragment selectedFragment = null;
            int itemId = item.getItemId();

            // Log the item ID to check which item is selected
            Log.d("BottomNavigation", "Selected Item ID: " + itemId);

            if (itemId == R.id.nav_catalog) {
                selectedFragment = new CatalogFragment();
            }  else if (itemId == R.id.nav_inbox) {
                selectedFragment = new TailorInboxFragment();
            } else if (itemId == R.id.nav_order) {
                selectedFragment = new tailororder();
            } else if (itemId == R.id.nav_home) {
                selectedFragment = new homet();
                return true; // Prevents reloading the same fragment
            }else if (itemId == R.id.nav_Custorder) {
                selectedFragment = new TailorOrderFragment();
                return true; // Prevents reloading the same fragment
            }

            // Log if a fragment is selected
            if (selectedFragment != null) {
                Log.d("BottomNavigation", "Loading Fragment: " + selectedFragment.getClass().getSimpleName());
                loadFragment(selectedFragment);
            } else {
                Log.d("BottomNavigation", "No Fragment Selected");
            }

            return true;
        });

        return view;
    }

    private void openImagePicker() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, PICK_IMAGE_REQUEST);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == Activity.RESULT_OK && data != null && data.getData() != null) {
            imageUri = data.getData();
            imageView.setImageURI(imageUri);
        }
    }

    private void checkIfDesignCodeIsUniqueAndAddCatalog() {
        String designCode = designCodeEditText.getText().toString().trim();

        // Check if design code is unique
        catalogRef.orderByChild("designCode").equalTo(designCode).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    Toast.makeText(getContext(), "Design code already exists", Toast.LENGTH_SHORT).show();
                } else {
                    addCatalogProduct();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(getContext(), "Error checking design code", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void addCatalogProduct() {
        String designCode = designCodeEditText.getText().toString().trim();
        String price = priceEditText.getText().toString().trim();
        int selectedGenderId = genderRadioGroup.getCheckedRadioButtonId(); // Get the selected radio button ID

        String gender = ""; // Initialize gender as an empty string

        // Determine the gender based on the selected radio button ID
        if (selectedGenderId == R.id.maleRadioButton) {
            gender = "Male"; // Assuming you have a radio button with this ID for Male
        } else if (selectedGenderId == R.id.femaleRadioButton) {
            gender = "Female"; // Assuming you have a radio button with this ID for Female
        }

        // Check for empty fields and image URI
        if (TextUtils.isEmpty(designCode) || TextUtils.isEmpty(price) || imageUri == null || TextUtils.isEmpty(gender)) {
            Toast.makeText(getContext(), "Please fill in all fields and select an image", Toast.LENGTH_SHORT).show();
            return;
        }

        // Upload image to Firebase Storage
        uploadImageToFirebase(imageUri, designCode, price, gender);
    }
    private void uploadImageToFirebase(Uri imageUri, String designCode, String price, String gender) {
        if (imageUri == null) {
            Toast.makeText(getContext(), "Image URI is null", Toast.LENGTH_SHORT).show();
            return;
        }

        String uniqueKey = FirebaseDatabase.getInstance().getReference().push().getKey();
        StorageReference storageRef = firebaseStorage.getReference().child("catalog_images").child(userId).child(uniqueKey);

        storageRef.putFile(imageUri)
                .addOnSuccessListener(taskSnapshot -> {
                    // Get the image URL after successful upload
                    storageRef.getDownloadUrl().addOnSuccessListener(uri -> {
                        String imageUrl = uri.toString();

                        // Create CatalogProduct object and store in Firebase Database
                        CatalogProduct catalogProduct = new CatalogProduct(designCode, gender, price, imageUrl);
                        catalogRef.child(designCode).setValue(catalogProduct)
                                .addOnSuccessListener(aVoid -> {
                                    Toast.makeText(getContext(), "Catalog product added successfully", Toast.LENGTH_SHORT).show();
                                    // Clear input fields after adding
                                    clearInputFields();
                                    // Reload catalog products
                                    loadCatalogProducts();
                                })
                                .addOnFailureListener(e -> Toast.makeText(getContext(), "Failed to add catalog product to database", Toast.LENGTH_SHORT).show());
                    }).addOnFailureListener(e -> {
                        Toast.makeText(getContext(), "Failed to get image URL from Firebase", Toast.LENGTH_SHORT).show();
                    });
                }).addOnFailureListener(e -> {
                    Toast.makeText(getContext(), "Failed to upload image to Firebase Storage", Toast.LENGTH_SHORT).show();
                });
    }

    private void loadCatalogProducts() {
        catalogRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                catalogProducts.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    CatalogProduct product = snapshot.getValue(CatalogProduct.class);
                    if (product != null) {
                        catalogProducts.add(product);
                    }
                }
                catalogsAdapter.notifyDataSetChanged(); // Notify adapter of data changes
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(getContext(), "Error loading catalog products", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void clearInputFields() {
        designCodeEditText.setText("");
        priceEditText.setText("");
        imageView.setImageURI(null); // Clear image view
        imageUri = null; // Reset image URI
        genderRadioGroup.clearCheck(); // Clear gender selection
    }
    private void deleteCatalogProduct(String designCode) {
        new AlertDialog.Builder(getContext())
                .setTitle("Delete Product")
                .setMessage("Are you sure you want to delete this product?")
                .setPositiveButton("Yes", (dialog, which) -> {
                    // User confirmed to delete
                    catalogRef.child(designCode).removeValue()
                            .addOnSuccessListener(aVoid -> {
                                Toast.makeText(getContext(), "Catalog product deleted successfully", Toast.LENGTH_SHORT).show();
                                loadCatalogProducts(); // Reload the catalog to reflect changes
                            })
                            .addOnFailureListener(e -> {
                                Toast.makeText(getContext(), "Failed to delete catalog product", Toast.LENGTH_SHORT).show();
                            });
                })
                .setNegativeButton("No", (dialog, which) -> {
                    // User canceled the delete operation
                    dialog.dismiss();
                })
                .show();
    }
    private void loadFragment(Fragment fragment) {
        // Load the selected fragment into the container
        FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
        fragmentManager.beginTransaction()
                .replace(R.id.fragment_container, fragment) // Assuming you have a FrameLayout or similar in your main layout
                .addToBackStack(null) // Optional: add to back stack to allow back navigation
                .commit();
    }
}

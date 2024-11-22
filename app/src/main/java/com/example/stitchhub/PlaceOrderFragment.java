package com.example.stitchhub;

import android.app.AlertDialog;
import android.graphics.Rect;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

    public class PlaceOrderFragment extends Fragment {
        private static final String TAG = "PlaceOrderFragment";
        private static final String CATALOG_PATH = "catalog";
        private static final String ORDERS_PATH = "orders";
        private static final String TAILORS_PATH = "tailors";

        private RadioGroup radioGroupGender;
        private RecyclerView recyclerViewCatalog, recyclerViewPreviousWork;
        private ImageButton buttonMessage;
        private Button buttonSelectMeasurement, buttonPlaceOrder, buttonCustomOrder;
        private TextView textViewOrderPrice;
        private EditText address, contact, estimatedDelivery;
        private String tailorUid;
        private CatalogAdapter catalogAdapter;
        private double totalOrderPrice = 0.00;
        private Set<CatalogProduct> selectedProducts = new HashSet<>();

        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            // Get tailor UID from arguments
            if (getArguments() != null) {
                tailorUid = getArguments().getString("TAILOR_UID");
                Log.d(TAG, "Tailor UID: " + tailorUid);
            } else {
                Log.e(TAG, "Tailor UID is not provided.");
                Toast.makeText(getContext(), "Error: Tailor UID is required.", Toast.LENGTH_SHORT).show();
                getActivity().onBackPressed(); // Navigate back if no UID is provided
            }

            // Initialize adapter with the click listener
            catalogAdapter = new CatalogAdapter(
                    this::updateOrderPrice,   // Select product callback
                    this::deselectProduct,     // Deselect product callback
                    this::openFullImage        // Open full image callback
            );
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
            View view = inflater.inflate(R.layout.fragment_place_order, container, false);

            // Initialize UI elements
            address = view.findViewById(R.id.editTextAddress);
            contact = view.findViewById(R.id.editTextPhone);
            estimatedDelivery = view.findViewById(R.id.editTextEstimatedDelivery);
            radioGroupGender = view.findViewById(R.id.radioGroupGender);
            recyclerViewCatalog = view.findViewById(R.id.recyclerViewCatalog);
            buttonMessage = view.findViewById(R.id.buttonMessage);
            buttonSelectMeasurement = view.findViewById(R.id.buttonSelectMeasurement);
            buttonPlaceOrder = view.findViewById(R.id.buttonPlaceOrder);
            textViewOrderPrice = view.findViewById(R.id.textViewOrderPrice);
            ImageButton buttonBack = view.findViewById(R.id.buttonBack);
            buttonCustomOrder = view.findViewById(R.id.buttonCustomOrder);

            // Setup RecyclerViews
            recyclerViewCatalog.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
            recyclerViewCatalog.setAdapter(catalogAdapter);

            // Set up gender selection listener
            radioGroupGender.setOnCheckedChangeListener((group, checkedId) -> {
                String gender = checkedId == R.id.radioButtonMen ? "Male" : "Female";
                Log.d(TAG, "Selected gender: " + gender);
                loadCatalogData(gender);
            });
            final View rootView = getView();
            if (rootView != null) {
                rootView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                    @Override
                    public void onGlobalLayout() {
                        Rect r = new Rect();
                        rootView.getWindowVisibleDisplayFrame(r);
                        int heightDiff = rootView.getRootView().getHeight() - (r.bottom - r.top);
                        if (heightDiff > 100) { // if more than 100 pixels, its probably a keyboard...
                            // Adjust layout margin or padding
                            buttonPlaceOrder.setVisibility(View.VISIBLE);
                        } else {
                            // Reset to original
                            buttonPlaceOrder.setVisibility(View.VISIBLE);
                        }
                    }
                });
            }
            // Load initial catalog data for default gender (Male)
            loadCatalogData("Male");

            // Set up button click listeners
            buttonCustomOrder.setOnClickListener(v -> navigateToCustomOrderFragment());
            buttonMessage.setOnClickListener(v -> openChatScreen());
            buttonSelectMeasurement.setOnClickListener(v -> navigateToMeasurementFragmentIfNeeded());
            buttonPlaceOrder.setOnClickListener(v -> placeOrderIfNeeded());
            buttonBack.setOnClickListener(v -> getParentFragmentManager().popBackStack());

            return view;
        }
        private void navigateToCustomOrderFragment() {
            // Navigate to CustomOrderFragment
            Fragment customOrderFragment = new CustomerOrderFragment(); // Create an instance of CustomOrderFragment
            FragmentTransaction transaction = getParentFragmentManager().beginTransaction();
            transaction.replace(R.id.fragment_container, customOrderFragment); // Replace the current fragment with CustomOrderFragment
            transaction.addToBackStack(null); // Add the transaction to the back stack
            transaction.commit(); // Commit the transaction
        }
        private void loadCatalogData(String gender) {
            if (tailorUid == null) {
                Log.e(TAG, "Tailor UID is null.");
                return;
            }

            Log.d(TAG, "Loading catalog data for gender: " + gender);
            DatabaseReference catalogRef = FirebaseDatabase.getInstance().getReference(CATALOG_PATH)
                    .child(tailorUid);

            // Load catalog data for the specific gender
            catalogRef.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    List<CatalogProduct> catalogProducts = new ArrayList<>();

                    if (dataSnapshot.exists()) {
                        for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                            CatalogProduct product = snapshot.getValue(CatalogProduct.class);

                            // Check if the product's gender matches the requested gender
                            if (product != null && product.getGender().equalsIgnoreCase(gender)) {
                                catalogProducts.add(product);
                            }
                        }
                        catalogAdapter.setCatalogProducts(catalogProducts);
                    } else {
                        Log.e(TAG, "No catalog data found for this tailor.");
                        Toast.makeText(getContext(), "No catalog data available.", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    Log.e(TAG, "Database error: " + databaseError.getMessage());
                }
            });
        }
        private void updateOrderPrice(CatalogProduct product) {
            if (product != null && selectedProducts.add(product)) {
                try {
                    double productPrice = Double.parseDouble(product.getPrice());
                    Log.d(TAG, "Adding product price: " + productPrice);
                    totalOrderPrice += productPrice;
                    textViewOrderPrice.setText(String.format("Order Price: AED %.2f", totalOrderPrice));
                } catch (NumberFormatException e) {
                    Log.e(TAG, "Invalid price format: " + product.getPrice(), e);
                }
            }
        }

        private void deselectProduct(CatalogProduct product) {
            if (product != null && selectedProducts.remove(product)) {
                try {
                    double productPrice = Double.parseDouble(product.getPrice());
                    Log.d(TAG, "Subtracting product price: " + productPrice);
                    totalOrderPrice -= productPrice;
                    textViewOrderPrice.setText(String.format("Order Price: AED %.2f", totalOrderPrice));
                } catch (NumberFormatException e) {
                    Log.e(TAG, "Invalid price format: " + product.getPrice(), e);
                }
            }
        }

        private void openFullImage(CatalogProduct product) {
            AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
            View dialogView = getLayoutInflater().inflate(R.layout.dialog_full_image, null);
            ImageView imageView = dialogView.findViewById(R.id.fullImageView);

            Glide.with(getContext()).load(product.getImageUrl()).into(imageView);

            builder.setView(dialogView)
                    .setNegativeButton("Close", (dialog, which) -> dialog.dismiss())
                    .create()
                    .show();
        }

        private void navigateToMeasurementFragmentIfNeeded() {
            if (selectedProducts.isEmpty()) {
                Toast.makeText(getContext(), "Please select at least one product to continue.", Toast.LENGTH_SHORT).show();
                return;
            }

            CatalogProduct selectedProduct = selectedProducts.iterator().next();
            String productGender = selectedProduct.getGender();
            navigateToMeasurementFragment(productGender);
        }

        private void navigateToMeasurementFragment(String gender) {
            Fragment measurementFragment = "Male".equalsIgnoreCase(gender) ? new MeasurementFragment() : new FemaleMeasurementFragment();
            FragmentTransaction transaction = getParentFragmentManager().beginTransaction();
            transaction.replace(R.id.fragment_container, measurementFragment);
            transaction.addToBackStack(null);
            transaction.commit();

            // Set measurement result listener
            if (measurementFragment instanceof MeasurementFragment) {
                ((MeasurementFragment) measurementFragment).setMeasurementResultListener(this::placeOrderWithMeasurements);
            } else if (measurementFragment instanceof FemaleMeasurementFragment) {
                ((FemaleMeasurementFragment) measurementFragment).setMeasurementResultListener(this::placeOrderWithMeasurements);
            }
        }


        private void placeOrderIfNeeded() {
            // Validate if any products are selected
            if (selectedProducts.isEmpty()) {
                Toast.makeText(getContext(), "Please select at least one product to place the order.", Toast.LENGTH_SHORT).show();
                return;
            }

            // Validate address, contact, and delivery fields
            if (!isOrderDetailsValid()) {
                Toast.makeText(getContext(), "Please complete all required fields: address, contact, and delivery information.", Toast.LENGTH_SHORT).show();
                return;
            }

            // Proceed to place the order if all validations are passed
            placeOrderWithMeasurements(0, 0, 0, 0, 0); // Pass customerUid to the method
        }
        private boolean isOrderDetailsValid() {
            // Validate address, contact, and estimated delivery
            String enteredAddress = address.getText().toString().trim();
            String enteredContact = contact.getText().toString().trim();
            String enteredEstimatedDelivery = estimatedDelivery.getText().toString().trim();

            // Simple validation to check if these fields are filled
            return !enteredAddress.isEmpty() && !enteredContact.isEmpty() && !enteredEstimatedDelivery.isEmpty();
        }

        private void placeOrderWithMeasurements(double shirtLength, double shoulder, double sleeves, double chest, double trouserLength) {
            String enteredAddress = address.getText().toString().trim();
            String enteredContact = contact.getText().toString().trim();
            String enteredEstimatedDelivery = estimatedDelivery.getText().toString().trim();

            // Validate order details
            if (!isOrderDetailsValid()) {
                Toast.makeText(getContext(), "Please complete the required address, contact, and delivery information.", Toast.LENGTH_SHORT).show();
                return;
            }

            List<CatalogProduct> productList = new ArrayList<>(selectedProducts);
            FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
            Order order = new Order(tailorUid, productList, enteredAddress, enteredContact, enteredEstimatedDelivery, totalOrderPrice, shirtLength, shoulder, sleeves, chest, trouserLength, OrderStatus.PENDING);
            String currentUserId = currentUser.getUid();

            DatabaseReference ordersRef = FirebaseDatabase.getInstance().getReference(ORDERS_PATH).child(currentUserId);

            // Save order to Firebase
            ordersRef.push().setValue(order)
                    .addOnSuccessListener(aVoid -> {
                        Toast.makeText(getContext(), "Order placed successfully.", Toast.LENGTH_SHORT).show();
                        // Clear selections and reset price after order placement
                    })
                    .addOnFailureListener(e -> {
                        Log.e(TAG, "Order placement failed: " + e.getMessage());
                        Toast.makeText(getContext(), "Failed to place order. Please try again.", Toast.LENGTH_SHORT).show();
                    });
        }

        private void resetOrder() {
            selectedProducts.clear();
            totalOrderPrice = 0.00;
            textViewOrderPrice.setText("Order Price:PKR 0.00");
            address.setText("");
            contact.setText("");
            estimatedDelivery.setText("");
            Log.d(TAG, "Order reset to initial state.");
        }

        private void openChatScreen() {
            DatabaseReference tailorRef = FirebaseDatabase.getInstance().getReference(TAILORS_PATH).child(tailorUid);

            tailorRef.child("email").addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()) {
                        String tailorEmail = dataSnapshot.getValue(String.class);
                        if (tailorEmail != null) {
                            FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
                            if (currentUser != null) {
                                String userEmail = currentUser.getEmail();
                                if (userEmail != null) {
                                    String chatId = createChatIdFromEmails(userEmail, tailorEmail);

                                    // Pass chat details to the ChatFragment
                                    ChatFragment chatFragment = new ChatFragment();
                                    Bundle args = new Bundle();
                                    args.putString("CHAT_ID", chatId);
                                    args.putString("RECEIVER_UID", tailorUid);
                                    args.putString("tailorEmail", tailorEmail);
                                    chatFragment.setArguments(args);

                                    // Replace current fragment with ChatFragment
                                    FragmentManager fragmentManager = getParentFragmentManager();
                                    fragmentManager.beginTransaction()
                                            .replace(R.id.fragment_container, chatFragment)
                                            .addToBackStack(null)
                                            .commit();
                                } else {
                                    Log.e(TAG, "Current user's email is null.");
                                    Toast.makeText(getContext(), "Failed to retrieve your email.", Toast.LENGTH_SHORT).show();
                                }
                            } else {
                                Log.e(TAG, "Current user is null.");
                                Toast.makeText(getContext(), "User not authenticated.", Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            Log.e(TAG, "Tailor email is null.");
                            Toast.makeText(getContext(), "Tailor email not found.", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(getContext(), "Tailor email not found.", Toast.LENGTH_SHORT).show();
                    }
                }


                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    Log.e(TAG, "Database error: " + databaseError.getMessage());
                    Toast.makeText(getContext(), "Failed to retrieve tailor email.", Toast.LENGTH_SHORT).show();
                }
            });
        }
        private String createChatIdFromEmails(String userEmail, String tailorEmail) {
            // Replace special characters in emails to prevent issues in Firebase paths
            String sanitizedUserEmail = userEmail.replaceAll("[^a-zA-Z0-9]", "");
            String sanitizedTailorEmail = tailorEmail.replaceAll("[^a-zA-Z0-9]", "");

            // Create a consistent chat ID based on the two emails
            if (sanitizedUserEmail.compareTo(sanitizedTailorEmail) < 0) {
                return sanitizedUserEmail + "_" + sanitizedTailorEmail;
            } else {
                return sanitizedTailorEmail + "_" + sanitizedUserEmail;
            }
        }
    }
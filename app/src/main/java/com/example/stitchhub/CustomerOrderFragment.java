package com.example.stitchhub;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;


public class CustomerOrderFragment extends Fragment {

    private Button btnUploadDesign, btnSelectDate, btnSubmitOrder;
    private EditText etPriceRange;
    private TextInputEditText etAddress;  // Declare the address field
    private Uri designUri;
    private Date selectedDate;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_customer_order, container, false);

        btnUploadDesign = view.findViewById(R.id.btnUploadDesign);
        btnSelectDate = view.findViewById(R.id.btnSelectDate);
        btnSubmitOrder = view.findViewById(R.id.btnSubmitOrder);
        etPriceRange = view.findViewById(R.id.etPriceRange);
        etAddress = view.findViewById(R.id.etAddress);  // Initialize the address field

        btnUploadDesign.setOnClickListener(v -> uploadDesign());
        btnSelectDate.setOnClickListener(v -> selectDate());
        btnSubmitOrder.setOnClickListener(v -> submitOrder());

        return view;
    }

    private void uploadDesign() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("image/*");
        startActivityForResult(intent, 100);
    }

    private void selectDate() {
        Calendar calendar = Calendar.getInstance();
        DatePickerDialog datePickerDialog = new DatePickerDialog(requireContext(),
                (view, year, month, dayOfMonth) -> {
                    Calendar selected = Calendar.getInstance();
                    selected.set(year, month, dayOfMonth);
                    selectedDate = selected.getTime();
                    Toast.makeText(getContext(), "Date Selected: " + selectedDate, Toast.LENGTH_SHORT).show();
                },
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH));
        datePickerDialog.show();
    }

    private void submitOrder() {
        String priceRange = etPriceRange.getText().toString().trim();
        String address = etAddress.getText().toString().trim();  // Get the address input

        if (designUri == null || selectedDate == null || priceRange.isEmpty() || address.isEmpty()) {
            Toast.makeText(getContext(), "Please complete all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        // Format selectedDate to String
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        String formattedDate = dateFormat.format(selectedDate);

        // Upload data to Firebase
        DatabaseReference database = FirebaseDatabase.getInstance().getReference("Customorders");
        String orderId = database.push().getKey();

        CustomOrder order = new CustomOrder(
                orderId,
                FirebaseAuth.getInstance().getUid(),
                designUri.toString(),
                formattedDate, // Pass the formatted date string
                Double.parseDouble(priceRange),
                address // Include address
        );

        database.child(orderId).setValue(order)
                .addOnSuccessListener(aVoid -> Toast.makeText(getContext(), "Order Submitted", Toast.LENGTH_SHORT).show())
                .addOnFailureListener(e -> Toast.makeText(getContext(), "Failed to submit order", Toast.LENGTH_SHORT).show());
    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 100 && resultCode == Activity.RESULT_OK && data != null) {
            designUri = data.getData();
            Toast.makeText(getContext(), "Design Selected", Toast.LENGTH_SHORT).show();
        }
    }
}
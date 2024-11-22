package com.example.stitchhub;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.List;

public class OrderAdapter extends RecyclerView.Adapter<OrderAdapter.OrderViewHolder> {

    private List<Order> orderList;
    private static final String TAG = "OrderAdapter";

    public OrderAdapter(List<Order> orderList) {
        this.orderList = orderList;
    }

    @NonNull
    @Override
    public OrderViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.order_item_layout, parent, false);
        return new OrderViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull OrderViewHolder holder, int position) {
        Order order = orderList.get(position);
        holder.bind(order);
    }

    @Override
    public int getItemCount() {
        return orderList.size();
    }

    public static class OrderViewHolder extends RecyclerView.ViewHolder {

        TextView contactTextView, addressTextView, estimatedDeliveryTextView, totalPriceTextView;
        TextView shirtLengthTextView, shoulderTextView, sleevesTextView, chestTextView, trouserLengthTextView, orderStatusTextView,design;
        Button acceptButton, rejectButton, completeButton;

        public OrderViewHolder(View itemView) {
            super(itemView);
            contactTextView = itemView.findViewById(R.id.text_contact);
            addressTextView = itemView.findViewById(R.id.text_address);
            estimatedDeliveryTextView = itemView.findViewById(R.id.text_estimated_delivery);
            totalPriceTextView = itemView.findViewById(R.id.text_total_price);
            shirtLengthTextView = itemView.findViewById(R.id.text_shirt_length);
            shoulderTextView = itemView.findViewById(R.id.text_shoulder);
            sleevesTextView = itemView.findViewById(R.id.text_sleeves);
            chestTextView = itemView.findViewById(R.id.text_chest);
            trouserLengthTextView = itemView.findViewById(R.id.text_trouser_length);
            orderStatusTextView = itemView.findViewById(R.id.text_order_status);
design=itemView.findViewById(R.id.Design_Code);
            acceptButton = itemView.findViewById(R.id.accept_button);
            rejectButton = itemView.findViewById(R.id.reject_button);
            completeButton = itemView.findViewById(R.id.complete_button);
        }

        public void bind(Order order) {
            contactTextView.setText("Contact: " + order.getContact());
            addressTextView.setText("Address: " + order.getAddress());
            estimatedDeliveryTextView.setText("Estimated delivery: " + order.getEstimatedDelivery());
            totalPriceTextView.setText("Total Price: PKR " + order.getTotalPrice());

            // Setting measurements
            shirtLengthTextView.setText("Shirt Length: " + order.getShirtLength());
            shoulderTextView.setText("Shoulder Length: " + order.getShoulder());
            sleevesTextView.setText("Sleeve: " + order.getSleeves());
            chestTextView.setText("Chest: " + order.getChest());
            trouserLengthTextView.setText("Trouser Length: " + order.getTrouserLength());

            List<CatalogProduct> products = order.getProducts();

            // Display order status
            orderStatusTextView.setText(order.getOrderStatus().toString());
            StringBuilder designCodes = new StringBuilder();
            for (CatalogProduct product : products) {
                designCodes.append(product.getDesignCode()).append(", ");
            }

            // Remove the trailing comma and space
            if (designCodes.length() > 0) {
                designCodes.setLength(designCodes.length() - 2);
            }

            design.setText("Design Codes: " + designCodes.toString());
            // Handle button visibility based on order status
            switch (order.getOrderStatus()) {
                case PENDING:
                    acceptButton.setVisibility(View.VISIBLE);
                    rejectButton.setVisibility(View.VISIBLE);
                    completeButton.setVisibility(View.GONE);
                    break;
                case ACCEPTED:
                    acceptButton.setVisibility(View.GONE);
                    rejectButton.setVisibility(View.GONE);
                    completeButton.setVisibility(View.VISIBLE);
                    break;
                case COMPLETED:
                    acceptButton.setVisibility(View.GONE);
                    rejectButton.setVisibility(View.GONE);
                    completeButton.setVisibility(View.GONE);
                    break;
            }

            // Handle button clicks
            acceptButton.setOnClickListener(v -> {
                Log.d(TAG, "Accept button clicked");
                updateOrderStatus(order, OrderStatus.ACCEPTED);
            });

            rejectButton.setOnClickListener(v -> {
                Log.d(TAG, "Reject button clicked");
                updateOrderStatus(order, OrderStatus.REJECTED);
            });

            completeButton.setOnClickListener(v -> {
                Log.d(TAG, "Complete button clicked");
                updateOrderStatus(order, OrderStatus.COMPLETED);
            });
        }

        private void updateOrderStatus(Order order, OrderStatus newStatus) {
            DatabaseReference ordersRef = FirebaseDatabase.getInstance().getReference("orders");

            // Query orders based on the tailorUid to find the right order
            ordersRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    boolean orderFound = false;

                    // Iterate through customers
                    for (DataSnapshot customerSnapshot : dataSnapshot.getChildren()) {
                        // Iterate through orders under each customer
                        for (DataSnapshot orderSnapshot : customerSnapshot.getChildren()) {
                            Order currentOrder = orderSnapshot.getValue(Order.class);

                            // Check if the order matches the details of the order we want to update
                            if (currentOrder != null &&
                                    currentOrder.getTailorUid().equals(order.getTailorUid()) &&
                                    currentOrder.getContact().equals(order.getContact()) &&
                                    currentOrder.getEstimatedDelivery().equals(order.getEstimatedDelivery()) &&
                                    currentOrder.getTotalPrice() == order.getTotalPrice()) { // You can add other unique fields for validation

                                orderFound = true;

                                // Update the order status
                                orderSnapshot.getRef().child("status").setValue(newStatus)
                                        .addOnSuccessListener(aVoid -> Log.d(TAG, "Order status updated to: " + newStatus))
                                        .addOnFailureListener(e -> Log.e(TAG, "Failed to update order status: " + e.getMessage()));
                                break; // Stop iterating once the order is found
                            }
                        }
                        if (orderFound) break; // Stop iterating over customers if the order is found
                    }

                    if (!orderFound) {
                        Log.e(TAG, "No matching order found for update.");
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    Log.e(TAG, "Database error: " + databaseError.getMessage());
                }
            });
        }
}}
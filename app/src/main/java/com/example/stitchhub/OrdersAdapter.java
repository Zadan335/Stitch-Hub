package com.example.stitchhub;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class OrdersAdapter extends RecyclerView.Adapter<OrdersAdapter.OrderViewHolder> {

    private List<Order> orders;
    private Context context;
    private OnOrderClickListener onOrderClickListener;

    public interface OnOrderClickListener {
        void onViewDetailsClick(Order order);
    }

    public OrdersAdapter(List<Order> orders, Context context, OnOrderClickListener listener) {
        this.orders = orders;
        this.context = context;
        this.onOrderClickListener = listener;
    }

    @NonNull
    @Override
    public OrderViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_order, parent, false);
        return new OrderViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull OrderViewHolder holder, int position) {
        Order order = orders.get(position);

        // Set the order details in the view holder
        holder.textViewOrderTitle.setText("Order #" + (position + 1)); // Customize as needed
        holder.textViewTotalPrice.setText("Total Price: PKR " + order.getTotalPrice());
        holder.textViewEstimatedDelivery.setText("Estimated Delivery: " + order.getEstimatedDelivery());
        holder.textViewStatus.setText("Status: " + order.getStatus());
        List<CatalogProduct> products = order.getProducts();
        StringBuilder designCodes = new StringBuilder();
        for (CatalogProduct product : products) {
            designCodes.append(product.getDesignCode()).append(", ");
        }

        // Remove the trailing comma and space
        if (designCodes.length() > 0) {
            designCodes.setLength(designCodes.length() - 2);
        }

        holder.textViewDesignCodes.setText("Design Codes: " + designCodes.toString());

        // Set click listener for view details button
        holder.textViewDesignCodes.setText("Design Codes: " + designCodes.toString());

        // Set click listener for view details button

    }

    @Override
    public int getItemCount() {
        return orders.size();
    }

    static class OrderViewHolder extends RecyclerView.ViewHolder {
        TextView textViewOrderTitle;
        TextView textViewTotalPrice;
        TextView textViewEstimatedDelivery;
        TextView textViewDesignCodes; // Add this for design codes

        TextView textViewStatus;
        Button buttonViewDetails;

        OrderViewHolder(View itemView) {
            super(itemView);
            textViewOrderTitle = itemView.findViewById(R.id.textViewOrderTitle);
            textViewTotalPrice = itemView.findViewById(R.id.textViewTotalPrice);
            textViewEstimatedDelivery = itemView.findViewById(R.id.textViewEstimatedDelivery);
            textViewStatus = itemView.findViewById(R.id.textViewStatus);
            textViewDesignCodes = itemView.findViewById(R.id.textViewDesignCodes); // Initialize the design code TextView

        }
    }
}
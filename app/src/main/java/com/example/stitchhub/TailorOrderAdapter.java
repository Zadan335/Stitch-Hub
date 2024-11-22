package com.example.stitchhub;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.List;

public class TailorOrderAdapter extends  RecyclerView.Adapter<TailorOrderAdapter.OrderViewHolder> {

private final List<CustomOrder> orders;
private final Context context;

public TailorOrderAdapter(List<CustomOrder> orders, Context context) {
        this.orders = orders;
        this.context = context;
        }

@NonNull
@Override
public OrderViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_tailor_order, parent, false);
        return new OrderViewHolder(view);
        }

@Override
public void onBindViewHolder(@NonNull OrderViewHolder holder, int position) {
        CustomOrder order = orders.get(position);
        holder.tvCustomerName.setText("Customer: " + order.getCustomerId());  // Assuming customer name is stored by customerId
        holder.tvPriceRange.setText("Price Range: $" + order.getPriceRange());
        holder.tvDate.setText("Date: " + order.getAppointmentDate());

        holder.btnViewDesign.setOnClickListener(v -> {
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(order.getDesignUri()));
        context.startActivity(intent);
        });

        holder.btnAccept.setOnClickListener(v -> handleOrderResponse(order, true));
        holder.btnReject.setOnClickListener(v -> handleOrderResponse(order, false));
        }

private void handleOrderResponse(CustomOrder order, boolean isAccepted) {
        DatabaseReference orderRef = FirebaseDatabase.getInstance().getReference("orders").child(order.getOrderId());
        orderRef.child("status").setValue(isAccepted ? "Accepted" : "Rejected")
        .addOnSuccessListener(aVoid -> {
        order.setStatus(isAccepted ? "Accepted" : "Rejected");
        Toast.makeText(context, "Order " + (isAccepted ? "Accepted" : "Rejected"), Toast.LENGTH_SHORT).show();
        })
        .addOnFailureListener(e -> Toast.makeText(context, "Failed to update order status", Toast.LENGTH_SHORT).show());
        }

@Override
public int getItemCount() {
        return orders.size();
        }

static class OrderViewHolder extends RecyclerView.ViewHolder {
    TextView tvCustomerName, tvPriceRange, tvDate;
    Button btnViewDesign, btnAccept, btnReject;

    public OrderViewHolder(@NonNull View itemView) {
        super(itemView);
        tvCustomerName = itemView.findViewById(R.id.tvCustomerName);
        tvPriceRange = itemView.findViewById(R.id.tvPriceRange);
        tvDate = itemView.findViewById(R.id.tvDate);
        btnViewDesign = itemView.findViewById(R.id.btnViewDesign);
        btnAccept = itemView.findViewById(R.id.btnAccept);
        btnReject = itemView.findViewById(R.id.btnReject);
    }
}
}
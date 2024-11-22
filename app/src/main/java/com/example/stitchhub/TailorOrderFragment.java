package com.example.stitchhub;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;


public class TailorOrderFragment extends Fragment {

    private RecyclerView rvTailorOrders;
    private TailorOrderAdapter adapter;
    private List<CustomOrder> orderList;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_tailor_order, container, false);

        rvTailorOrders = view.findViewById(R.id.rvTailorOrders);
        rvTailorOrders.setLayoutManager(new LinearLayoutManager(getContext()));

        orderList = new ArrayList<>();
        adapter = new TailorOrderAdapter(orderList, requireContext());
        rvTailorOrders.setAdapter(adapter);

        fetchOrders();

        return view;
    }

    private void fetchOrders() {
        DatabaseReference ordersRef = FirebaseDatabase.getInstance().getReference("Customorders");
        ordersRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                orderList.clear();
                for (DataSnapshot orderSnap : snapshot.getChildren()) {
                    CustomOrder order = orderSnap.getValue(CustomOrder.class);
                    if (order != null && order.getTailorId() == null && order.getStatus().equals("Pending")) {
                        orderList.add(order);
                    }
                }
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(getContext(), "Failed to fetch orders.", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
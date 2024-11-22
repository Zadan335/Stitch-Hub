package com.example.stitchhub;


import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.List;

public class TailorAdapter extends RecyclerView.Adapter<TailorAdapter.TailorViewHolder> {

    private Context context;
    private List<Tailor> tailorList;
    private OnTailorClickListener onTailorClickListener;

    public TailorAdapter(Context context, List<Tailor> tailorList, OnTailorClickListener onTailorClickListener) {
        this.context = context;
        this.tailorList = tailorList;
        this.onTailorClickListener = onTailorClickListener;
    }

    @NonNull
    @Override
    public TailorViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_tailor, parent, false);
        return new TailorViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TailorViewHolder holder, int position) {
        Tailor tailor = tailorList.get(position);

        // Set tailor name, email, and address
        holder.textViewTailorName.setText(tailor.getName() != null ? tailor.getName() : "Unknown");
        holder.textViewTailorEmail.setText(tailor.getEmail() != null ? tailor.getEmail() : "No email available");
        holder.textViewTailorAddress.setText(tailor.getAddress() != null ? tailor.getAddress() : "No address available");

        // Load the tailor profile image
        if (tailor.getProfilePictureUrl() != null && !tailor.getProfilePictureUrl().isEmpty()) {
            // Using Glide to load the image from the URL
            Glide.with(context)
                    .load(tailor.getProfilePictureUrl())
                    .placeholder(R.drawable.user) // Set a default placeholder image
                    .error(R.drawable.user) // Set an image if there's an error loading
                    .into(holder.imageViewTailor);
        } else {
            // Set a default image if there's no image URL
            holder.imageViewTailor.setImageResource(R.drawable.user);
        }

        // Set click listener to navigate to PlaceOrderFragment
        holder.itemView.setOnClickListener(v -> {
            if (onTailorClickListener != null) {
                onTailorClickListener.onTailorClick(tailor);
            }
        });
    }

    @Override
    public int getItemCount() {
        return tailorList.size();
    }

    class TailorViewHolder extends RecyclerView.ViewHolder {
        ImageView imageViewTailor;
        TextView textViewTailorName;
        TextView textViewTailorEmail;
        TextView textViewTailorAddress;

        TailorViewHolder(@NonNull View itemView) {
            super(itemView);
            imageViewTailor = itemView.findViewById(R.id.imageViewTailor);
            textViewTailorName = itemView.findViewById(R.id.textViewTailorName);
            textViewTailorEmail = itemView.findViewById(R.id.textViewTailorEmail);
            textViewTailorAddress = itemView.findViewById(R.id.textViewTailorAddress);
        }
    }

    // Interface to handle tailor click events
    public interface OnTailorClickListener {
        void onTailorClick(Tailor tailor);
    }
}
package com.example.stitchhub;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class CatalogAdapter extends RecyclerView.Adapter<CatalogAdapter.CatalogViewHolder> {
    private List<CatalogProduct> catalogProducts = new ArrayList<>();
    private OnItemClickListener onItemClickListener;
    private OnItemLongClickListener onItemLongClickListener;
    private OnItemDoubleClickListener onItemDoubleClickListener;
    private Set<Integer> selectedItems = new HashSet<>();  // To track selected items

    // Interface for click events
    public interface OnItemClickListener {
        void onItemClick(CatalogProduct product);
    }

    // Interface for long-click events
    public interface OnItemLongClickListener {
        void onItemLongClick(CatalogProduct product);
    }

    // Interface for double-click events
    public interface OnItemDoubleClickListener {
        void onItemDoubleClick(CatalogProduct product);
    }

    // Constructor to set the listeners
    public CatalogAdapter(OnItemClickListener clickListener, OnItemLongClickListener longClickListener, OnItemDoubleClickListener doubleClickListener) {
        this.onItemClickListener = clickListener;
        this.onItemLongClickListener = longClickListener;
        this.onItemDoubleClickListener = doubleClickListener;
    }

    @NonNull
    @Override
    public CatalogViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_catalog, parent, false);
        return new CatalogViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CatalogViewHolder holder, int position) {
        CatalogProduct product = catalogProducts.get(position);

        // Set the image, design code, and price in the ViewHolder
        holder.textViewDesignCode.setText(product.getDesignCode());
        holder.textViewPrice.setText("Price: " + product.getPrice());

        // Load image using Glide
        Glide.with(holder.itemView.getContext())
                .load(product.getImageUrl())
                .into(holder.imageViewCatalogProduct);

        // Highlight selected items
        holder.itemView.setBackgroundColor(selectedItems.contains(position) ? Color.LTGRAY : Color.WHITE);

        // Handle click events
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            private static final long DOUBLE_CLICK_TIME_DELTA = 400; // Time window for double-click
            long lastClickTime = 0;

            @Override
            public void onClick(View v) {
                long clickTime = System.currentTimeMillis();
                if (clickTime - lastClickTime < DOUBLE_CLICK_TIME_DELTA) {
                    // Double-click detected
                    if (selectedItems.contains(position)) {
                        selectedItems.remove(position); // Deselect item
                    } else {
                        selectedItems.add(position); // Select item
                    }
                    notifyItemChanged(position); // Update view
                    if (onItemDoubleClickListener != null) {
                        onItemDoubleClickListener.onItemDoubleClick(product);
                    }
                } else {
                    // Single-click detected
                    selectedItems.add(position); // Select item
                    notifyItemChanged(position); // Update view
                    if (onItemClickListener != null) {
                        onItemClickListener.onItemClick(product);
                    }
                }
                lastClickTime = clickTime;
            }
        });

        // Handle long-press (show full image in dialog)
        holder.itemView.setOnLongClickListener(v -> {
            if (onItemLongClickListener != null) {
                onItemLongClickListener.onItemLongClick(product);
                showFullImageDialog(holder.itemView.getContext(), product.getImageUrl());
            }
            return true;
        });
    }

    @Override
    public int getItemCount() {
        return catalogProducts.size();
    }

    public void setCatalogProducts(List<CatalogProduct> catalogProducts) {
        this.catalogProducts = catalogProducts;
        notifyDataSetChanged();
    }

    // Define the ViewHolder
    class CatalogViewHolder extends RecyclerView.ViewHolder {
        ImageView imageViewCatalogProduct;
        TextView textViewDesignCode;
        TextView textViewPrice;

        public CatalogViewHolder(@NonNull View itemView) {
            super(itemView);
            // Initialize the views
            imageViewCatalogProduct = itemView.findViewById(R.id.imageViewCatalogProduct);
            textViewDesignCode = itemView.findViewById(R.id.textViewDesignCode);
            textViewPrice = itemView.findViewById(R.id.textViewPrice);
        }
    }

    // Method to show full image in a dialog
    private void showFullImageDialog(Context context, String imageUrl) {
        Dialog dialog = new Dialog(context, android.R.style.Theme_Black_NoTitleBar_Fullscreen);
        dialog.setContentView(R.layout.dialog_full_image); // Assuming you have a layout for full-screen image

        ImageView fullImageView = dialog.findViewById(R.id.fullImageView); // ImageView in the dialog layout
        Glide.with(context).load(imageUrl).into(fullImageView); // Load image into the dialog

        dialog.show();
    }
}
package com.example.stitchhub;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.List;

public class CatalogsAdapter extends RecyclerView.Adapter<CatalogsAdapter.CatalogViewHolder> {
    private final List<CatalogProduct> productList;
    private final OnEditClickListener editClickListener;
    private final OnDeleteClickListener deleteClickListener;

    public CatalogsAdapter(List<CatalogProduct> productList, OnEditClickListener editClickListener, OnDeleteClickListener deleteClickListener) {
        this.productList = productList;
        this.editClickListener = editClickListener;
        this.deleteClickListener = deleteClickListener;
    }

    @NonNull
    @Override
    public CatalogViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.catalog_items, parent, false);
        return new CatalogViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CatalogViewHolder holder, int position) {
        CatalogProduct product = productList.get(position);
        holder.bind(product, editClickListener, deleteClickListener);
    }

    @Override
    public int getItemCount() {
        return productList.size();
    }

    static class CatalogViewHolder extends RecyclerView.ViewHolder {
        private final TextView designCodeTextView;
        private final TextView priceTextView;
        private final ImageView productImageView;

        private final Button deleteButton;

        public CatalogViewHolder(@NonNull View itemView) {
            super(itemView);
            designCodeTextView = itemView.findViewById(R.id.designCodeTextView);
            priceTextView = itemView.findViewById(R.id.priceTextView);
            productImageView = itemView.findViewById(R.id.productImageView);
            deleteButton = itemView.findViewById(R.id.deleteButton);
        }

        public void bind(CatalogProduct product, OnEditClickListener editClickListener, OnDeleteClickListener deleteClickListener) {
            designCodeTextView.setText(product.getDesignCode());
            priceTextView.setText(product.getPrice());

            // Load image with error handling
            if (product.getImageUrl() != null) {
                Glide.with(itemView.getContext())
                        .load(product.getImageUrl())
                        .placeholder(R.drawable.user) // Set a placeholder image
                        .error(R.drawable.user) // Set an error image
                        .into(productImageView);
            } else {
                productImageView.setImageResource(R.drawable.user); // Set error image if URL is null
            }

            deleteButton.setOnClickListener(v -> deleteClickListener.onDeleteClick(product));
        }
    }

    public interface OnEditClickListener {
        void onEditClick(CatalogProduct product);
    }

    public interface OnDeleteClickListener {
        void onDeleteClick(CatalogProduct product);
    }
}
package com.example.stitchhub;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;


public class InboxAdapter extends RecyclerView.Adapter<InboxAdapter.InboxViewHolder> {
    private List<InboxItem> inboxList;
    private OnChatClickListener chatClickListener;

    public interface OnChatClickListener {
        void onChatClick(InboxItem inboxItem);
    }

    public InboxAdapter(List<InboxItem> inboxList, OnChatClickListener chatClickListener) {
        this.inboxList = inboxList;
        this.chatClickListener = chatClickListener;
    }

    @NonNull
    @Override
    public InboxViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_chat_inbox, parent, false);
        return new InboxViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull InboxViewHolder holder, int position) {
        InboxItem inboxItem = inboxList.get(position);
        holder.bind(inboxItem, chatClickListener);
    }

    @Override
    public int getItemCount() {
        return inboxList.size();
    }



        static class InboxViewHolder extends RecyclerView.ViewHolder {
            private ImageView profileImageView;
            private TextView emailTextView;
            private TextView lastMessageTextView;

            public InboxViewHolder(@NonNull View itemView) {
                super(itemView);
                profileImageView = itemView.findViewById(R.id.senderProfilePic); // Updated ID
                emailTextView = itemView.findViewById(R.id.senderEmail); // Updated ID
                lastMessageTextView = itemView.findViewById(R.id.lastmess); // Updated ID
            }

            public void bind(InboxItem inboxItem, OnChatClickListener chatClickListener) {
                // Load the profile image using a library like Glide or Picasso
                // Example with Glide (make sure to include Glide in your build.gradle)
                // Glide.with(itemView.getContext())
                //    .load(inboxItem.getProfileImageUrl()) // Get the profile image URL from the InboxItem
                //    .placeholder(R.drawable.user) // Placeholder image
                //    .into(profileImageView);

                emailTextView.setText(inboxItem.getSenderEmail());
                lastMessageTextView.setText(inboxItem.getLastMessage());
                itemView.setOnClickListener(v -> chatClickListener.onChatClick(inboxItem));
            }
        }}
package com.example.stitchhub;

import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.VideoView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

public class VideoPlayerFragment extends Fragment {
    private VideoView videoView;
    private String videoUrl; // Replace with your Firebase video URL

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_video_player, container, false);

        videoView = view.findViewById(R.id.videoView);
        Button backButton = view.findViewById(R.id.backButton);

        // Get video URL from Firebase Storage
        videoUrl = "https://firebasestorage.googleapis.com/v0/b/stitch-hub-87261.appspot.com/o/measurementvideo%2FHow%20To%20Take%20Measurements%20For%20Shalwar%20Kameez_6%20Easy%20Steps_Send%20your%20Size%20Online.mp4?alt=media&token=797f3367-a44f-4730-b743-b6783003c586"; // Update this to your actual video URL
        playVideo(videoUrl);

        // Back button to return to the previous screen
        backButton.setOnClickListener(v -> getParentFragmentManager().popBackStack());

        return view;
    }

    private void playVideo(String videoUrl) {
        Uri videoUri = Uri.parse(videoUrl);
        videoView.setVideoURI(videoUri);
        videoView.setOnPreparedListener(mp -> {
            mp.setLooping(true); // Loop the video if needed
            videoView.start(); // Start the video playback
        });
    }
}
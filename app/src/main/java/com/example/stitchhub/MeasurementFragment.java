package com.example.stitchhub;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;
import android.widget.VideoView;

import androidx.fragment.app.Fragment;

import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

public class MeasurementFragment extends Fragment {
    private EditText editTextShirtLength, editTextShoulder, editTextSleeves, editTextChest, editTextTrouserLength;
    private Button buttonSaveMeasurements;
    private ImageButton videoTutorialButton;
    private VideoView videoView;

    private MeasurementResultListener measurementResultListener;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_measurement, container, false);

        // Initialize UI elements
        editTextShirtLength = view.findViewById(R.id.shirt_length);
        editTextShoulder = view.findViewById(R.id.shoulder_length);
        editTextSleeves = view.findViewById(R.id.armlength);
        editTextChest = view.findViewById(R.id.chestm);
        editTextTrouserLength = view.findViewById(R.id.below_trouser_length);
        buttonSaveMeasurements = view.findViewById(R.id.submit_button);
        videoTutorialButton = view.findViewById(R.id.videoTutorialButton);

        buttonSaveMeasurements.setOnClickListener(v -> saveMeasurements());

        videoTutorialButton.setOnClickListener(v -> {
            VideoPlayerFragment videoPlayerFragment = new VideoPlayerFragment();
            getParentFragmentManager().beginTransaction()
                    .replace(R.id.fragment_container, videoPlayerFragment) // Replace with your container ID
                    .addToBackStack(null) // Optional: add to back stack to allow back navigation
                    .commit();
        });

        videoView = view.findViewById(R.id.videoView);
        return view;
    }

    private void saveMeasurements() {
        try {
            double shirtLength = Double.parseDouble(editTextShirtLength.getText().toString());
            double shoulder = Double.parseDouble(editTextShoulder.getText().toString());
            double sleeves = Double.parseDouble(editTextSleeves.getText().toString());
            double chest = Double.parseDouble(editTextChest.getText().toString());
            double trouserLength = Double.parseDouble(editTextTrouserLength.getText().toString());

            // Validate measurements (optional)
            if (isValidMeasurement(shirtLength, shoulder, sleeves, chest, trouserLength)) {
                if (measurementResultListener != null) {
                    measurementResultListener.onMeasurementsSelected(shirtLength, shoulder, sleeves, chest, trouserLength);
                }

                // Navigate back
                getParentFragmentManager().popBackStack();
            } else {
                Toast.makeText(getContext(), "Please enter measurements in valid ranges.", Toast.LENGTH_SHORT).show();
            }
        } catch (NumberFormatException e) {
            Toast.makeText(getContext(), "Please enter valid numeric measurements.", Toast.LENGTH_SHORT).show();
        }
    }

    // Method to validate if the measurements are within acceptable ranges for male
    private boolean isValidMeasurement(double shirtLength, double shoulder, double sleeves, double chest, double trouserLength) {
        // Ranges for valid male clothing measurements in inches (these can be adjusted)
        return (shirtLength >= 24 && shirtLength <= 48) // Typical male shirt length in inches
                && (shoulder >= 14 && shoulder <= 30) // Typical male shoulder width in inches
                && (sleeves >= 18 && sleeves <= 36) // Typical male sleeve length in inches
                && (chest >= 32 && chest <= 70) // Typical male chest size in inches
                && (trouserLength >= 30 && trouserLength <= 50); // Typical male trouser length in inches
    }

    public void setMeasurementResultListener(MeasurementFragment.MeasurementResultListener listener) {
        this.measurementResultListener = listener;
    }

    public interface MeasurementResultListener {
        void onMeasurementsSelected(double shirtLength, double shoulder, double sleeves, double chest, double trouserLength);
    }
}
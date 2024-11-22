package com.example.stitchhub;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;


public class FemaleMeasurementFragment extends Fragment {
    private EditText editTextChest, editTextArm, editTextShoulder, editTextShirtLength, editTextTrouserLength;
    private Button buttonSaveMeasurements;
    private MeasurementResultListener measurementResultListener;
    private ImageButton videoTutorialButton;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_female_measurement, container, false);

        // Initialize UI elements
        editTextChest = view.findViewById(R.id.chest);
        editTextArm = view.findViewById(R.id.Arm);
        editTextShoulder = view.findViewById(R.id.Shoulder);
        editTextShirtLength = view.findViewById(R.id.ShirtLength);
        editTextTrouserLength = view.findViewById(R.id.trouser);
        buttonSaveMeasurements = view.findViewById(R.id.submit_button);
        videoTutorialButton = view.findViewById(R.id.videoTutorialButton);

        videoTutorialButton.setOnClickListener(v -> {
            VideoPlayerFragment videoPlayerFragment = new VideoPlayerFragment();
            getParentFragmentManager().beginTransaction()
                    .replace(R.id.fragment_container, videoPlayerFragment) // Replace with your container ID
                    .addToBackStack(null) // Optional: add to back stack to allow back navigation
                    .commit();
        });

        buttonSaveMeasurements.setOnClickListener(v -> saveMeasurements());

        return view;
    }

    private void saveMeasurements() {
        try {
            double shirtLength = Double.parseDouble(editTextShirtLength.getText().toString());
            double shoulder = Double.parseDouble(editTextShoulder.getText().toString());
            double sleeves = Double.parseDouble(editTextArm.getText().toString());
            double chest = Double.parseDouble(editTextChest.getText().toString());
            double trouserLength = Double.parseDouble(editTextTrouserLength.getText().toString());

            // Validate measurements with the valid range for each
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

    // Method to validate if the measurements are within acceptable ranges
    private boolean isValidMeasurement(double shirtLength, double shoulder, double sleeves, double chest, double trouserLength) {
        // Ranges for valid female clothing measurements in inches (these can be adjusted)
        return (shirtLength >= 20 && shirtLength <= 40) // Typical shirt length in inches
                && (shoulder >= 12 && shoulder <= 24) // Typical shoulder width in inches
                && (sleeves >= 15 && sleeves <= 30) // Typical arm/sleeve length in inches
                && (chest >= 28 && chest <= 60) // Typical chest size in inches
                && (trouserLength >= 28 && trouserLength <= 45); // Typical trouser length in inches
    }

    public void setMeasurementResultListener(FemaleMeasurementFragment.MeasurementResultListener listener) {
        this.measurementResultListener = listener;
    }

    public interface MeasurementResultListener {
        void onMeasurementsSelected(double shirtLength, double shoulder, double sleeves, double chest, double trouserLength);
    }
}
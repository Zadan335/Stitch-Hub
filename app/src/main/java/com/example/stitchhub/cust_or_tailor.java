package com.example.stitchhub;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;

import com.airbnb.lottie.LottieAnimationView;


public class cust_or_tailor extends Fragment {


    public cust_or_tailor () {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_cust_or_tailor, container, false);
        LottieAnimationView lottieAnimationView = view.findViewById(R.id.animation_view);
        Button buttonCustomer = view.findViewById(R.id.button_customer);
        buttonCustomer.setOnClickListener(v -> navigateToLogin());
        Button buttonTailor = view.findViewById(R.id.button_tailor);
        buttonTailor.setOnClickListener(v -> navigateTotailor());
        Animation fadeIn = AnimationUtils.loadAnimation(getContext(), R.anim.fade_in);
        lottieAnimationView.startAnimation(fadeIn);
        return view;
    }

    private void navigateToLogin() {
        Fragment loginFragment = new login();
        FragmentTransaction transaction = getFragmentManager().beginTransaction();
       transaction.setCustomAnimations(R.anim.fade_in2, R.anim.fade_out);

        transaction.replace(R.id.fragment_container, loginFragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }
    private void navigateTotailor() {
          Fragment Logintailor = new Logintailor();
        FragmentTransaction transaction = getFragmentManager().beginTransaction();
       transaction.replace(R.id.fragment_container,Logintailor );
      transaction.setCustomAnimations(R.anim.fade_in2, R.anim.fade_out);

        transaction.addToBackStack(null);
        transaction.commit();
    }
}
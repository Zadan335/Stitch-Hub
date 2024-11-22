package com.example.stitchhub;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.viewpager.widget.PagerAdapter;

public class  ImageSliderAdapter extends PagerAdapter {

    private Context context;
    private int[] images;

    public ImageSliderAdapter(Context context, int[] images) {
        this.context = context;
        this.images = images;
    }

    @Override
    public int getCount() {
        return images.length; // Returns the number of images
    }


    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
        return view == object; // Determines whether the view is associated with the object (image)
    }

    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, int position) {
        // Inflate the layout for each image
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.slider_image_item, container, false);

        // Set the image resource
        ImageView imageView = view.findViewById(R.id.sliderImage);
        imageView.setImageResource(images[position]);

        // Add the view to the ViewPager
        container.addView(view);
        return view;
    }

    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        // Remove the view from the ViewPager
        container.removeView((View) object);
    }
}
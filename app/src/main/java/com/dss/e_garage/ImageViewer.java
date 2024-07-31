package com.dss.e_garage;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.ImageView;

import com.bumptech.glide.Glide;

public class ImageViewer extends AppCompatActivity {
ImageView iv;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_viewer);

        iv=findViewById(R.id.iv);
        Glide.with(this)
                .load(getIntent().getStringExtra("url"))
                .into(iv);
    }
}
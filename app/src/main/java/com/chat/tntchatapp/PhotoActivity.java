package com.chat.tntchatapp;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.bumptech.glide.Glide;
import com.github.chrisbanes.photoview.PhotoView;

import androidx.appcompat.widget.Toolbar;

public class PhotoActivity extends AppCompatActivity {
    Toolbar toolbar;
    PhotoView photoView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_photo);

        String url=getIntent().getStringExtra("url");
        toolbar=findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        photoView=findViewById(R.id.photo_view);

        Glide.with(PhotoActivity.this).load(url).into(photoView);


    }
}

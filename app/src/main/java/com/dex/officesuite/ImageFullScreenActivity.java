package com.dex.officesuite;

import androidx.appcompat.app.AppCompatActivity;

import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

public class ImageFullScreenActivity extends AppCompatActivity {

    SharedPreferences theme = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        theme = getSharedPreferences("com.dex.officesuite", MODE_PRIVATE);
        if(theme.getBoolean("themeset",true)) {
            setTheme(R.style.LightTheme);
        }
        else {
            setTheme(R.style.DarkTheme);
        }
        setContentView(R.layout.activity_image_full_screen);


        ImageView fullScreenImageView = findViewById(R.id.fullScreenImageView);

        Uri uri=getIntent().getData();

        if(uri!=null){
            fullScreenImageView.setImageURI(uri);
        }

        fullScreenImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }
}
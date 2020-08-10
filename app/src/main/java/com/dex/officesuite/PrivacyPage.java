package com.dex.officesuite;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.NavUtils;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;

public class PrivacyPage extends AppCompatActivity {

    SharedPreferences theme = null;
    ConstraintLayout transparencyCL, storageCL, cameraCL, audioCL;
    ImageView transparencyBTN, storageBTN, cameraBTN, audioBTN;
    private int transparencyRot = 0, storageRot = 0, cameraRot, audioRot;
    ConstraintLayout transparency, storage, camera, audio;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        theme = getSharedPreferences("com.example.hab", MODE_PRIVATE);
        if(theme.getBoolean("themeset",true)) {
            setTheme(R.style.LightThemeBar);
        }
        else {
            setTheme(R.style.DarkThemeBar);
        }
        setContentView(R.layout.activity_privacy_page);

        transparencyBTN = findViewById(R.id.transparency_Button);
        transparencyCL = findViewById(R.id.transparency_Description_CL);
        transparency = findViewById(R.id.transparency);
        storageBTN = findViewById(R.id.storage_button);
        storageCL = findViewById(R.id.storage_Description_CL);
        storage = findViewById(R.id.storage);
        cameraBTN = findViewById(R.id.camera_button);
        cameraCL= findViewById(R.id.camera_Description_CL);
        camera = findViewById(R.id.camera);
        audioBTN = findViewById(R.id.audio_button);
        audioCL= findViewById(R.id.audio_Description_CL);
        audio = findViewById(R.id.audio);


        transparency.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                transparencyRot %= 360;
                float fromRotation = transparencyRot;
                float toRotation = transparencyRot += 180;
                final RotateAnimation rotateAnim = new RotateAnimation(fromRotation, toRotation, (float) (transparencyBTN.getWidth()/2.0), (float) (transparencyBTN.getHeight()/2.0));
                rotateAnim.setDuration(500);
                rotateAnim.setFillAfter(true);
                transparencyBTN.startAnimation(rotateAnim);
                if(transparencyCL.getVisibility()==View.VISIBLE){
                    transparencyCL.setVisibility(View.GONE);
                }
                else {
                    transparencyCL.setVisibility(View.VISIBLE);
                }
            }
        });


        storage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                storageRot %= 360;
                float fromRotation = storageRot;
                float toRotation = storageRot += 180;
                final RotateAnimation rotateAnim = new RotateAnimation(fromRotation, toRotation, (float) (storageBTN.getWidth()/2.0), (float) (storageBTN.getHeight()/2.0));
                rotateAnim.setDuration(500);
                rotateAnim.setFillAfter(true);
                storageBTN.startAnimation(rotateAnim);
                if(storageCL.getVisibility()==View.VISIBLE){
                    storageCL.setVisibility(View.GONE);
                }
                else {
                    storageCL.setVisibility(View.VISIBLE);
                }
            }
        });


        camera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cameraRot %= 360;
                float fromRotation = cameraRot;
                float toRotation = cameraRot += 180;
                final RotateAnimation rotateAnim = new RotateAnimation(fromRotation, toRotation, (float) (cameraBTN.getWidth()/2.0), (float) (cameraBTN.getHeight()/2.0));
                rotateAnim.setDuration(500);
                rotateAnim.setFillAfter(true);
                cameraBTN.startAnimation(rotateAnim);
                if(cameraCL.getVisibility()==View.VISIBLE){
                    cameraCL.setVisibility(View.GONE);
                }
                else {
                    cameraCL.setVisibility(View.VISIBLE);
                }
            }
        });


        audio.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                audioRot %= 360;
                float fromRotation = audioRot;
                float toRotation = audioRot += 180;
                final RotateAnimation rotateAnim = new RotateAnimation(fromRotation, toRotation, (float) (audioBTN.getWidth()/2.0), (float) (audioBTN.getHeight()/2.0));
                rotateAnim.setDuration(500);
                rotateAnim.setFillAfter(true);
                audioBTN.startAnimation(rotateAnim);
                if(audioCL.getVisibility()==View.VISIBLE){
                    audioCL.setVisibility(View.GONE);
                }
                else {
                    audioCL.setVisibility(View.VISIBLE);
                }
            }
        });

    }

    @Override
    public void onBackPressed() {
        Intent intent=new Intent(this,MainActivity.class);
        startActivity(intent);
        finish();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            NavUtils.navigateUpFromSameTask(this);
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
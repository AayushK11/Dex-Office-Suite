package com.dex.officesuite;

import androidx.appcompat.app.AppCompatActivity;

import android.animation.ObjectAnimator;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

public class SplashScreen extends AppCompatActivity {

    SharedPreferences prefs = null;
    SharedPreferences theme = null;
    ImageView logo;
    TextView appname, appbaseline, devname;

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
        setContentView(R.layout.activity_splash_screen);



        logo = findViewById(R.id.logo);
        prefs = getSharedPreferences("com.dex.officesuite", MODE_PRIVATE);
        appname = findViewById(R.id.appname);
        appbaseline = findViewById(R.id.appbaseline);
        devname = findViewById(R.id.devname);



        final Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                try{
                    Thread.sleep(1500);
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {

                            ObjectAnimator animation = ObjectAnimator.ofFloat(logo, "translationX", -225f);
                            ObjectAnimator fadeIn = ObjectAnimator.ofFloat(appname, "alpha", .0f, 1f);
                            ObjectAnimator fadeIn1 = ObjectAnimator.ofFloat(appbaseline, "alpha", .0f, 1f);
                            ObjectAnimator fadeIn2 = ObjectAnimator.ofFloat(devname, "alpha", .0f, 1f);

                            animation.setDuration(1500);
                            animation.start();

                            appname.setVisibility(View.VISIBLE);
                            appbaseline.setVisibility(View.VISIBLE);
                            devname.setVisibility(View.VISIBLE);

                            fadeIn.setDuration(1700);
                            fadeIn1.setDuration(1700);
                            fadeIn2.setDuration(1700);

                            fadeIn.start();
                            fadeIn1.start();
                            fadeIn2.start();
                        }
                    });
                    Thread.sleep(2500);

                }catch (InterruptedException e){
                    e.printStackTrace();
                }


                if(prefs.getBoolean("prefSplash",true)){
                    prefs.edit().putBoolean("prefSplash",false).apply();
                    Log.i("Splash Screen","---->Welcome Screen");
                    Intent intent = new Intent(SplashScreen.this, WelcomeScreen.class);
                    startActivity(intent);
                }
                else {
                    Log.i("Splash Screen", "---->Main Activity");
                    Intent intent = new Intent(SplashScreen.this, MainActivity.class);
                    startActivity(intent);
                }
                finish();
            }
        });
        thread.start();
    }
}

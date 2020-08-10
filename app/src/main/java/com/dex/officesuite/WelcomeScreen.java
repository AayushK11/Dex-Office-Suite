package com.dex.officesuite;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.MotionEvent;

import com.dex.officesuite.fragments.WelcomePage1;
import com.dex.officesuite.fragments.WelcomePage2;
import com.dex.officesuite.fragments.WelcomePage3;
import com.dex.officesuite.fragments.WelcomePage4;

public class WelcomeScreen extends AppCompatActivity {

    private float x1;
    FragmentManager fm = getSupportFragmentManager();
    final Fragment first = new WelcomePage1();
    final Fragment second = new WelcomePage2();
    final Fragment third = new WelcomePage3();
    final Fragment fourth = new WelcomePage4();
    SharedPreferences theme = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        theme = getSharedPreferences("com.example.hab", MODE_PRIVATE);
        if(theme.getBoolean("themeset",true)) {
            setTheme(R.style.LightTheme);
        }
        else {
            setTheme(R.style.DarkTheme);
        }
        setContentView(R.layout.activity_welcome_screen);


        FragmentTransaction fragmentTransaction = fm.beginTransaction();
        fragmentTransaction.replace(R.id.frame, first,"First");
        fragmentTransaction.commit();
    }

    public boolean onTouchEvent(MotionEvent event){
        switch(event.getAction())
        {
            case MotionEvent.ACTION_DOWN:
                x1 = event.getX();
                break;

            case MotionEvent.ACTION_UP:
                float x2 = event.getX();
                float deltaX = x2 - x1;

                Fragment firstFrag = fm.findFragmentByTag("First");
                Fragment secondFrag = fm.findFragmentByTag("Second");
                Fragment thirdFrag = fm.findFragmentByTag("Third");
                Fragment fourthFrag = fm.findFragmentByTag("Fourth");


                if(deltaX<0.0){

                    if(firstFrag!=null && firstFrag.isVisible()){
                        FragmentTransaction fragmentTransaction = fm.beginTransaction();
                        fragmentTransaction.setCustomAnimations(R.anim.enter_from_right, R.anim.exit_to_left, R.anim.enter_from_left, R.anim.exit_to_right);
                        fragmentTransaction.replace(R.id.frame, second,"Second");
                        fragmentTransaction.commit();
                    }

                    else if(secondFrag!=null && secondFrag.isVisible()){
                        FragmentTransaction fragmentTransaction = fm.beginTransaction();
                        fragmentTransaction.setCustomAnimations(R.anim.enter_from_right, R.anim.exit_to_left, R.anim.enter_from_left, R.anim.exit_to_right);
                        fragmentTransaction.replace(R.id.frame, third ,"Third");
                        fragmentTransaction.commit();
                    }

                    else if(thirdFrag!=null && thirdFrag.isVisible()){
                        FragmentTransaction fragmentTransaction = fm.beginTransaction();
                        fragmentTransaction.setCustomAnimations(R.anim.enter_from_right, R.anim.exit_to_left, R.anim.enter_from_left, R.anim.exit_to_right);
                        fragmentTransaction.replace(R.id.frame, fourth ,"Fourth");
                        fragmentTransaction.commit();
                    }

                    else if(fourthFrag!=null && fourthFrag.isVisible()){
                        Intent intent = new Intent(WelcomeScreen.this, MainActivity.class);
                        startActivity(intent);
                        finish();
                    }

                }
                else if(deltaX>0.0){

                    if(secondFrag!=null && secondFrag.isVisible()){
                        FragmentTransaction fragmentTransaction = fm.beginTransaction();
                        fragmentTransaction.setCustomAnimations(R.anim.enter_from_left, R.anim.exit_to_right, R.anim.enter_from_right, R.anim.exit_to_left);
                        fragmentTransaction.replace(R.id.frame, first ,"First");
                        fragmentTransaction.commit();
                    }

                    else if(thirdFrag!=null && thirdFrag.isVisible()){
                        FragmentTransaction fragmentTransaction = fm.beginTransaction();
                        fragmentTransaction.setCustomAnimations(R.anim.enter_from_left, R.anim.exit_to_right, R.anim.enter_from_right, R.anim.exit_to_left);
                        fragmentTransaction.replace(R.id.frame, second ,"Second");
                        fragmentTransaction.commit();
                    }

                    else if(fourthFrag!=null && fourthFrag.isVisible()){
                        FragmentTransaction fragmentTransaction = fm.beginTransaction();
                        fragmentTransaction.setCustomAnimations(R.anim.enter_from_left, R.anim.exit_to_right, R.anim.enter_from_right, R.anim.exit_to_left);
                        fragmentTransaction.replace(R.id.frame, third ,"Third");
                        fragmentTransaction.commit();
                    }
                }
        }
        return super.onTouchEvent(event);
    }
}
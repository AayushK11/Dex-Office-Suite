package com.dex.officesuite;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NavUtils;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.CheckBox;
import android.widget.CompoundButton;

public class SettingsPage extends AppCompatActivity {

    CheckBox theme_CB;
    SharedPreferences theme = null;

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
        setContentView(R.layout.activity_settings_page);


        theme_CB = findViewById(R.id.theme_CB);
        if(theme.getBoolean("themeset",true)) {
            theme_CB.setChecked(false);
        }
        else {
            theme_CB.setChecked(true);
        }

        theme_CB.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(theme_CB.isChecked()){
                    changeTheme("Dark");
                }
                else{
                    changeTheme("Light");
                }
            }
        });
    }

    public void changeTheme(String newTheme){
        if(newTheme.equalsIgnoreCase("Dark")){
            setTheme(R.style.DarkThemeBar);
            theme.edit().putBoolean("themeset",false).apply();
            recreate();
        }
        else if(newTheme.equalsIgnoreCase("Light")){
            setTheme(R.style.LightThemeBar);
            theme.edit().putBoolean("themeset",true).apply();
            recreate();
        }
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
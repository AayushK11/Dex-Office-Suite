package com.dex.officesuite;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;


public class MainActivity extends AppCompatActivity {

    SharedPreferences theme = null;
    CardView ocr,scanner_card;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        theme = getSharedPreferences("com.dex.officesuite", MODE_PRIVATE);
        if(theme.getBoolean("themeset",true)) {
            setTheme(R.style.LightThemeBar);
        }
        else {
            setTheme(R.style.DarkThemeBar);
        }

        setContentView(R.layout.activity_main);

        ocr = findViewById(R.id.ocr_card);
        ocr.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this,OcrActivity.class);
                startActivity(intent);
            }
        });

        scanner_card=findViewById(R.id.scanner);
        scanner_card.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this,ScannerActivity.class);
                startActivity(intent);
            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.home_overflow, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem menuItem){
        super.onOptionsItemSelected(menuItem);

        if(menuItem.getItemId() == R.id.voiceButton){
//            askRecordPerm();
        }
        else if(menuItem.getItemId() == R.id.privacy){
            Intent intent = new Intent(MainActivity.this, PrivacyPage.class);
            startActivity(intent);
        }
        else if(menuItem.getItemId() == R.id.settings){
            Intent intent = new Intent(MainActivity.this, SettingsPage.class);
            startActivity(intent);
        }
        return false;
    }
}
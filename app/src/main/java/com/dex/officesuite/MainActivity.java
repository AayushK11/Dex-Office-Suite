package com.dex.officesuite;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
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
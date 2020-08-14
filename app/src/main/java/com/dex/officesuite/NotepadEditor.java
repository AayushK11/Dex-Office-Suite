package com.dex.officesuite;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NavUtils;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.Toast;

import java.util.HashSet;
import java.util.Locale;
import java.util.Objects;

public class NotepadEditor extends AppCompatActivity {


    private static final int RECORD_CODE = 100 ;
    private static final int AUDIO_CODE = 101 ;
    SharedPreferences theme = null;
    EditText note_ET, title_ET;
    int noteID;


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
        setContentView(R.layout.activity_notepad_editor);

        note_ET = findViewById(R.id.note_ET);
        title_ET = findViewById(R.id.title_note);

        Intent intent = getIntent();
        noteID = intent.getIntExtra("noteID", -1);

        if(noteID != -1){
            note_ET.setText(NotepadActivity.notes.get(noteID));
            title_ET.setText(NotepadActivity.title.get(noteID));
        }
        else {
            NotepadActivity.notes.add("");
            NotepadActivity.title.add("");

            noteID = NotepadActivity.notes.size() -1;

            NotepadActivity.arrayAdapter.notifyDataSetChanged();
        }

        note_ET.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

                NotepadActivity.notes.set(noteID, String.valueOf(s));
                System.out.println(NotepadActivity.notes);
                System.out.println(noteID);
                System.out.println(s);

                SharedPreferences sharedPreferences = getApplicationContext().getSharedPreferences("com.dex.officesuite", Context.MODE_PRIVATE);
                HashSet<String> hashSet = new HashSet<>(NotepadActivity.notes);
                System.out.println(hashSet);
                sharedPreferences.edit().putStringSet("notes", hashSet).apply();
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        title_ET.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

                NotepadActivity.title.set(noteID, String.valueOf(s));
                NotepadActivity.arrayAdapter.notifyDataSetChanged();

                SharedPreferences sharedPreferences = getApplicationContext().getSharedPreferences("com.dex.officesuite", Context.MODE_PRIVATE);
                HashSet<String> hashSet = new HashSet<>(NotepadActivity.title);
                sharedPreferences.edit().putStringSet("titles", hashSet).apply();
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }

    @Override
    public void onBackPressed() {
        Intent intent=new Intent(this,NotepadActivity.class);
        startActivity(intent);
        finish();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.voice_overflow, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            if(title_ET.getText().toString().equals("")){
                if(note_ET.getText().toString().equals("")){
                    NotepadActivity.title.remove(noteID);
                    NotepadActivity.notes.remove(noteID);
                }
                else {
                    String note, title;
                    note = note_ET.getText().toString();
                    title = note;
                    title_ET.setText(title);
                    note_ET.setText("");
                    NotepadActivity.notes.set(noteID, note);
                    NotepadActivity.title.set(noteID, title);
                    SharedPreferences sharedPreferences = getApplicationContext().getSharedPreferences("com.dex.officesuite", Context.MODE_PRIVATE);
                    HashSet<String> hashSet = new HashSet<>(NotepadActivity.title);
                    sharedPreferences.edit().putStringSet("titles", hashSet).apply();
                    HashSet<String> hashSet1 = new HashSet<>(NotepadActivity.notes);
                    sharedPreferences.edit().putStringSet("notes", hashSet1).apply();
                }
            }
            NavUtils.navigateUpFromSameTask(this);
            finish();
            return true;
        }
        if (item.getItemId() == R.id.voiceButton) {
            askRecordPerm();
        }
        return super.onOptionsItemSelected(item);
    }

    private void askRecordPerm(){

        if(ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(this, new String[] {Manifest.permission.RECORD_AUDIO}, RECORD_CODE);
        }
        else{
            voiceAssistant();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults){
        if(requestCode == RECORD_CODE){
            if(grantResults.length > 0 && grantResults[0]==PackageManager.PERMISSION_GRANTED){
                voiceAssistant();
            }
            else {
                Toast.makeText(this,"Audio Permissions are required to start the Voice Assistant", Toast.LENGTH_SHORT).show();
            }
        }
    }

    public void voiceAssistant(){
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());
        intent.putExtra(RecognizerIntent.EXTRA_PROMPT, "Speak Now");
        try {
            startActivityForResult(intent, AUDIO_CODE);
        } catch (ActivityNotFoundException a) {
            Toast.makeText(getApplicationContext(), "Sorry your device is not supported", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == AUDIO_CODE) {
            if (resultCode == RESULT_OK) {
                String[] result = Objects.requireNonNull(data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS)).toArray(new String[0]);
                parseArrayList(result);
            }
        }
    }

    public void parseArrayList(String[] list){
        for(String word:list){
            if(title_ET.hasFocus()){
                String temp = title_ET.getText()+" "+word;
                title_ET.setText(temp);
            }
            else {
                String temp = note_ET.getText()+" "+word;
                note_ET.setText(temp);
            }
        }
    }
}
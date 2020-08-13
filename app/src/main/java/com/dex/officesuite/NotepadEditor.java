package com.dex.officesuite;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NavUtils;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.MenuItem;
import android.widget.EditText;

import java.util.HashSet;

public class NotepadEditor extends AppCompatActivity {


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
        return super.onOptionsItemSelected(item);
    }
}
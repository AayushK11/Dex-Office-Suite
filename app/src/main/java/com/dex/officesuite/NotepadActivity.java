package com.dex.officesuite;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.NavUtils;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.HashSet;

public class NotepadActivity extends AppCompatActivity {


    SharedPreferences theme = null;
    ListView listView;
    ConstraintLayout constraintLayout;
    ImageView imageView;
    static ArrayList<String> notes = new ArrayList<>();
    static ArrayList<String> title = new ArrayList<>();
    static ArrayAdapter<String> arrayAdapter;

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
        setContentView(R.layout.activity_notepad);

        constraintLayout = findViewById(R.id.layout_CL);
        listView = findViewById(R.id.listview);
        imageView = findViewById(R.id.plus_icon);

        HashSet<String> hashSet = (HashSet<String>)theme.getStringSet("notes", null);
        final HashSet<String> titleSet = (HashSet<String>)theme.getStringSet("titles", null);
        if(titleSet == null){
            constraintLayout.setVisibility(View.VISIBLE);
            listView.setVisibility(View.INVISIBLE);
        }
        else {
            assert hashSet != null;
            notes = new ArrayList<>(hashSet);
            title = new ArrayList<>(titleSet);
        }

        arrayAdapter = new ArrayAdapter<>(this, R.layout.notes_list_test, title);
        listView.setAdapter(arrayAdapter);

        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), NotepadEditor.class);
                startActivity(intent);
            }
        });

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(getApplicationContext(), NotepadEditor.class);
                intent.putExtra("noteID", position);
                startActivity(intent);
            }
        });

        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, final int position, long id) {

                AlertDialog.Builder builder = new AlertDialog.Builder(NotepadActivity.this);
                builder.setIcon(android.R.drawable.ic_dialog_alert);
                builder.setTitle("Delete");
                builder.setMessage("Are You Sure You Want To Delete this Note?");
                builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        notes.remove(position);
                        title.remove(position);
                        arrayAdapter.notifyDataSetChanged();
                        HashSet<String> hashSet = new HashSet<>(NotepadActivity.notes);
                        theme.edit().putStringSet("notes", hashSet).apply();
                        HashSet<String> hashSet1 = new HashSet<>(NotepadActivity.title);
                        theme.edit().putStringSet("titles", hashSet1).apply();
                    }
                });
                builder.setNegativeButton("No", null);
                builder.create().show();

                return true;
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        if(notes.isEmpty() && title.isEmpty()){
            constraintLayout.setVisibility(View.VISIBLE);
            listView.setVisibility(View.INVISIBLE);
        }
        else{
            constraintLayout.setVisibility(View.INVISIBLE);
            listView.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.new_note_overflow, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item){
        super.onOptionsItemSelected(item);

        if(item.getItemId() == R.id.addNote){
            Intent intent = new Intent(getApplicationContext(), NotepadEditor.class);
            startActivity(intent);
            return true;
        }

        if (item.getItemId() == android.R.id.home) {
            NavUtils.navigateUpFromSameTask(this);
            finish();
            return true;
        }
        return false;
    }

    @Override
    public void onBackPressed() {
        Intent intent=new Intent(this,MainActivity.class);
        startActivity(intent);
        finish();
    }
}
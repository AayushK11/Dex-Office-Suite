package com.dex.officesuite;

import androidx.annotation.ColorInt;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.NavUtils;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashSet;

public class TodoActivity extends AppCompatActivity {

    SharedPreferences theme = null;
    ListView listView, listView1;
    ConstraintLayout constraintLayout;
    TextView pending, completed;
    ImageView imageView;
    View divider;
    static ArrayList<String> checked = new ArrayList<>();
    static ArrayList<String> unchecked = new ArrayList<>();
    static ArrayAdapter<String> arrayAdapterChecked;
    static ArrayAdapter<String> arrayAdapterUnchecked;

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
        setContentView(R.layout.activity_todo);

        constraintLayout = findViewById(R.id.layout_CL);
        listView = findViewById(R.id.listview);
        listView1 = findViewById(R.id.listview1);
        imageView = findViewById(R.id.plus_icon);
        pending = findViewById(R.id.pendingTV);
        completed = findViewById(R.id.completedTV);
        divider = findViewById(R.id.divider);

        HashSet<String> checkSet = (HashSet<String>)theme.getStringSet("checked", null);
        HashSet<String> uncheckSet = (HashSet<String>)theme.getStringSet("unchecked", null);
        if (checkSet != null && uncheckSet !=null) {
            constraintLayout.setVisibility(View.INVISIBLE);
            listView.setVisibility(View.VISIBLE);
            listView1.setVisibility(View.VISIBLE);
            pending.setVisibility(View.VISIBLE);
            completed.setVisibility(View.VISIBLE);
            checked = new ArrayList<>(checkSet);
            unchecked = new ArrayList<>(uncheckSet);
        }
        else{
            constraintLayout.setVisibility(View.VISIBLE);
            listView.setVisibility(View.INVISIBLE);
            listView1.setVisibility(View.INVISIBLE);
            pending.setVisibility(View.GONE);
            completed.setVisibility(View.GONE);
        }


        arrayAdapterChecked = new ArrayAdapter<>(this, R.layout.notes_list_test, checked);
        arrayAdapterUnchecked = new ArrayAdapter<>(this, R.layout.todo_checked_list_test, unchecked);
        listView.setAdapter(arrayAdapterChecked);
        listView1.setAdapter(arrayAdapterUnchecked);

        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addTask();
            }
        });

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String temp = checked.get(position);
                checked.remove(position);
                unchecked.add(temp);
                arrayAdapterChecked.notifyDataSetChanged();
                arrayAdapterUnchecked.notifyDataSetChanged();
                HashSet<String> hashSet = new HashSet<>(TodoActivity.checked);
                theme.edit().putStringSet("checked", hashSet).apply();
                HashSet<String> hashSet1 = new HashSet<>(TodoActivity.unchecked);
                theme.edit().putStringSet("unchecked", hashSet1).apply();
            }
        });

        listView1.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String temp = unchecked.get(position);
                unchecked.remove(position);
                checked.add(temp);
                arrayAdapterChecked.notifyDataSetChanged();
                arrayAdapterUnchecked.notifyDataSetChanged();
                HashSet<String> hashSet = new HashSet<>(TodoActivity.checked);
                theme.edit().putStringSet("checked", hashSet).apply();
                HashSet<String> hashSet1 = new HashSet<>(TodoActivity.unchecked);
                theme.edit().putStringSet("unchecked", hashSet1).apply();
            }
        });

        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, final int position, long id) {
                AlertDialog.Builder builder = new AlertDialog.Builder(TodoActivity.this, R.style.AlertDialogCustom);
                builder.setIcon(android.R.drawable.ic_dialog_alert);
                builder.setTitle("Delete");
                builder.setMessage("Are You Sure You Want To Delete this task?");
                builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        checked.remove(position);
                        arrayAdapterChecked.notifyDataSetChanged();
                        HashSet<String> hashSet = new HashSet<>(TodoActivity.checked);
                        theme.edit().putStringSet("checked", hashSet).apply();
                    }
                });
                builder.setNegativeButton("No", null);
                builder.create().show();
                return true;
            }
        });

        listView1.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, final int position, long id) {
                AlertDialog.Builder builder = new AlertDialog.Builder(TodoActivity.this, R.style.AlertDialogCustom);
                builder.setIcon(android.R.drawable.ic_dialog_alert);
                builder.setTitle("Delete");
                builder.setMessage("Are You Sure You Want To Delete this task?");
                builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        unchecked.remove(position);
                        arrayAdapterUnchecked.notifyDataSetChanged();
                        HashSet<String> hashSet = new HashSet<>(TodoActivity.unchecked);
                        theme.edit().putStringSet("unchecked", hashSet).apply();
                    }
                });
                builder.setNegativeButton("No", null);
                builder.create().show();
                return true;
            }
        });
    }

    public void addTask(){
        AlertDialog.Builder builder = new AlertDialog.Builder(TodoActivity.this, R.style.AlertDialogCustom);
        builder.setTitle("Add Task");
        builder.setMessage("Task title");
        final EditText input = new EditText(TodoActivity.this);
        TypedValue typedValue = new TypedValue();
        Resources.Theme theme1 = this.getTheme();
        theme1.resolveAttribute(R.attr.textColor, typedValue, true);
        @ColorInt int color = typedValue.data;
        input.setTextColor(color);
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
        input.setLayoutParams(lp);
        builder.setView(input);
        builder.setCancelable(true);
        builder.setPositiveButton("Add", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                constraintLayout.setVisibility(View.INVISIBLE);
                listView.setVisibility(View.VISIBLE);
                listView1.setVisibility(View.VISIBLE);
                pending.setVisibility(View.VISIBLE);
                completed.setVisibility(View.VISIBLE);
                String task = input.getText().toString();
                checked.add(task);
                arrayAdapterChecked.notifyDataSetChanged();
                HashSet<String> hashSet = new HashSet<>(TodoActivity.checked);
                theme.edit().putStringSet("checked", hashSet).apply();
                HashSet<String> hashSet1 = new HashSet<>(TodoActivity.unchecked);
                theme.edit().putStringSet("unchecked", hashSet1).apply();
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        builder.create().show();
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
            addTask();
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
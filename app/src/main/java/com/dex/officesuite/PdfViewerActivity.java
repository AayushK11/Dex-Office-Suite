package com.dex.officesuite;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NavUtils;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Environment;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.util.ArrayList;

public class PdfViewerActivity extends AppCompatActivity {

    private static final int READ_PERM_CODE = 100;
    private static final int WRITE_PERM_CODE = 101;
    SharedPreferences theme = null;
    ListView listView;
    static ArrayList<File> pdfs = new ArrayList<>();
    static ArrayList<String> names = new ArrayList<>();
    static ArrayAdapter<String> arrayAdapter;
    ProgressBar progressBar;
    TextView textView;


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
        setContentView(R.layout.activity_pdf_viewer);

        listView = findViewById(R.id.listview);
        progressBar = findViewById(R.id.simpleProgressBar);
        textView = findViewById(R.id.loading);

        progressBar.setVisibility(View.VISIBLE);
        textView.setVisibility(View.VISIBLE);
        
        askReadPermissions();

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(getApplicationContext(), PdfViewerScreen.class);
                intent.putExtra("path", position);
                startActivity(intent);
            }
        });
    }

    private void askReadPermissions(){
        if(ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(this, new String[] {Manifest.permission.READ_EXTERNAL_STORAGE}, READ_PERM_CODE);
        }
        else{
            askWritePermissions();
        }
    }

    private void askWritePermissions(){
        if(ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(this, new String[] {Manifest.permission.WRITE_EXTERNAL_STORAGE}, WRITE_PERM_CODE);
        }
        else{
            ScanDirectory();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults){
        if(requestCode == READ_PERM_CODE){
            if(grantResults.length > 0 && grantResults[0]==PackageManager.PERMISSION_GRANTED){
                askWritePermissions();
            }
            else {
                Toast.makeText(this,"R/W Permissions are required to ensure smooth functioning of the app", Toast.LENGTH_SHORT).show();
            }
        }
        if(requestCode == WRITE_PERM_CODE){
            if(grantResults.length > 0 && grantResults[0]==PackageManager.PERMISSION_GRANTED){
                ScanDirectory();
            }
            else {
                Toast.makeText(this,"R/W Permissions are required to ensure smooth functioning of the app", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void ScanDirectory() {
        Search_Dir(Environment.getExternalStorageDirectory());
        arrayAdapter = new ArrayAdapter<>(this, R.layout.pdf_list_test, names);
        listView.setAdapter(arrayAdapter);
        progressBar.setVisibility(View.INVISIBLE);
        textView.setVisibility(View.INVISIBLE);
    }

    private void Search_Dir(File dir) {

        String pdfPattern = ".pdf";

        File[] FileList = dir.listFiles();

        if (FileList != null) {
            for (File file : FileList) {
                if (file.isDirectory()) {
                    Search_Dir(file);
                } else {
                    if (file.getName().endsWith(pdfPattern)) {
                        String filename = file.toString().substring(file.toString().lastIndexOf("/") + 1);
                        pdfs.add(file);
                        names.add(filename);
                    }
                }
            }
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
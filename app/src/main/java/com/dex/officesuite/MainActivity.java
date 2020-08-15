package com.dex.officesuite;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import java.util.Locale;
import java.util.Objects;


public class MainActivity extends AppCompatActivity {

    private static final int RECORD_CODE = 100;
    private static final int AUDIO_CODE = 101;
    SharedPreferences theme = null;
    CardView ocr,scanner, canvas, notepad, pdfViewer, todo;

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

        scanner=findViewById(R.id.scanner_card);
        scanner.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this,ScannerActivity.class);
                startActivity(intent);
            }
        });

        canvas=findViewById(R.id.canvas_card);
        canvas.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this,CanvasActivity.class);
                startActivity(intent);
            }
        });

        notepad=findViewById(R.id.notepad_card);
        notepad.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this,NotepadActivity.class);
                startActivity(intent);
            }
        });

        pdfViewer=findViewById(R.id.pdfViewer_card);
        pdfViewer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this,PdfViewerActivity.class);
                startActivity(intent);
            }
        });

        todo=findViewById(R.id.todo_card);
        todo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this,TodoActivity.class);
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
            askRecordPerm();
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
        String[] iptext = list[0].split(" ");
        String[] OCR = {"Recognise","OCR","Optical","Character","Recognition","Recognize","Text in Image","Text from Image"};
        String[] Scanner = {"Scan","Scanner","Convert","Make","Create Document","Create PDF","Image to PDF","Make PDF"};
        String[] Notes = {"Notes","Note","New Note","Notepad","Note it","Make a Note","Create Note","Create a note"};
        String[] Canvas = {"Draw","Color","Colour","Canvas","Design", "Drawing","Drawing Pad"};
        String[] PDFViewer = {"Show","View","Viewer","Open a PDF","Show a PDF"};
        String[] Todo = {"Task","Todo","To-do", "to do","New task", "New task list","New to do task","Open task list"};
        String[] Settings = {"Settings","Dark Mode","Dark", "Theme", "Light Mode", "Light"};
        String[] Privacy = {"Safety","Privacy","Notice","Privacy Notice","user", "data","User-data","Permissions","permission"};
        int count = 0;
        for(String word:iptext){
            for(String testcase:OCR){
                if(word.equalsIgnoreCase(testcase) && count==0){
                    count++;
                    Intent intent = new Intent(MainActivity.this,OcrActivity.class);
                    startActivity(intent);
                    break;
                }
            }
            for(String testcase:Scanner){
                if(word.equalsIgnoreCase(testcase) && count==0){
                    count++;
                    Intent intent = new Intent(MainActivity.this,ScannerActivity.class);
                    startActivity(intent);
                    break;
                }
            }
            for(String testcase:Notes){
                if(word.equalsIgnoreCase(testcase) && count==0){
                    count++;
                    Intent intent = new Intent(MainActivity.this,NotepadActivity.class);
                    startActivity(intent);
                    break;
                }
            }
            for(String testcase:Canvas){
                if(word.equalsIgnoreCase(testcase) && count==0){
                    count++;
                    Intent intent = new Intent(MainActivity.this,CanvasActivity.class);
                    startActivity(intent);
                    break;
                }
            }
            for(String testcase:PDFViewer){
                if(word.equalsIgnoreCase(testcase) && count==0){
                    count++;
                    Intent intent = new Intent(MainActivity.this,PdfViewerActivity.class);
                    startActivity(intent);
                    break;
                }
            }
            for(String testcase:Todo){
                if(word.equalsIgnoreCase(testcase) && count==0){
                    count++;
                    Intent intent = new Intent(MainActivity.this,TodoActivity.class);
                    startActivity(intent);
                    break;
                }
            }
            for(String testcase:Settings){
                if(word.equalsIgnoreCase(testcase) && count==0){
                    count++;
                    Intent intent = new Intent(MainActivity.this,SettingsPage.class);
                    startActivity(intent);
                    break;
                }
            }
            for(String testcase:Privacy){
                if(word.equalsIgnoreCase(testcase) && count==0){
                    count++;
                    Intent intent = new Intent(MainActivity.this,PrivacyPage.class);
                    startActivity(intent);
                    break;
                }
            }
        }
        if(count==0){
            Toast.makeText(this, "Function may not be valid",Toast.LENGTH_SHORT).show();
        }
    }

}
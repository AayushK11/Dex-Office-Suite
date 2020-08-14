package com.dex.officesuite;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NavUtils;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.MenuItem;

import com.github.barteksc.pdfviewer.PDFView;
import com.github.barteksc.pdfviewer.util.FitPolicy;

import java.io.File;

public class PdfViewerScreen extends AppCompatActivity {

    SharedPreferences theme = null;
    PDFView pdfView;

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
        setContentView(R.layout.activity_pdf_viewer_screen);

        Intent intent = getIntent();
        int position = intent.getIntExtra("path", -1);

        pdfView = findViewById(R.id.pdfView);

        if(position!=-1){
            File file = PdfViewerActivity.pdfs.get(position);
            pdfView.fromFile(file)
                    .enableSwipe(true)
                    .enableAntialiasing(true)
                    .swipeHorizontal(false)
                    .enableDoubletap(true)
                    .defaultPage(0)
                    .enableAnnotationRendering(false)
                    .password(null)
                    .scrollHandle(null)
                    .spacing(0)
                    .pageFitPolicy(FitPolicy.BOTH)
                    .load();
        }
    }

    @Override
    public void onBackPressed() {
        Intent intent=new Intent(this,PdfViewerActivity.class);
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
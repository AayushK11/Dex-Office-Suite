package com.dex.officesuite;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NavUtils;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Toast;

import java.util.UUID;

public class CanvasActivity extends AppCompatActivity {

    private ImageButton currpaint;
    private CanvasDrawer drawingView;
    SharedPreferences theme = null;

    @SuppressLint("UseCompatLoadingForDrawables")
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
        setContentView(R.layout.activity_canvas);

        drawingView = findViewById(R.id.DrawingView);
        ImageButton drawBTN = findViewById(R.id.draw_IMG);
        ImageButton newBTN = findViewById(R.id.new_IMG);
        ImageButton eraseBTN = findViewById(R.id.erase_IMG);
        ImageButton saveBTN = findViewById(R.id.save_IMG);
        LinearLayout paintLayout = findViewById(R.id.paint_colors);

        currpaint = (ImageButton) paintLayout.getChildAt(0);
        currpaint.setImageDrawable(getResources().getDrawable(R.drawable.paint_pressed));
        drawingView.setPaintColor(paintLayout.getChildAt(0).getTag().toString());

        drawBTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                drawingView.setupDrawing();
            }
        });

        eraseBTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                drawingView.startErase(true);
                drawingView.setBrushSize(drawingView.getLastBrushSize());
            }
        });

        newBTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(CanvasActivity.this, R.style.AlertDialogCustom);
                builder.setTitle("New Drawing");
                builder.setMessage("Start a new Drawing?");
                builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        drawingView.startNew();
                        dialog.dismiss();
                    }
                });
                builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });
                builder.create().show();
            }
        });

        saveBTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(CanvasActivity.this, R.style.AlertDialogCustom);
                builder.setTitle("Save Drawing");
                builder.setMessage("Do You Want to Save this Drawing?");
                builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        drawingView.setDrawingCacheEnabled(true);
                        String imgSaved = MediaStore.Images.Media.insertImage(getContentResolver(), drawingView.getDrawingCache(), UUID.randomUUID().toString() + ".png", "Drawing");
                        if (imgSaved != null) {
                            Toast.makeText(CanvasActivity.this, "Drawing Saved to Gallery", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(CanvasActivity.this, "Drawing Could Not be Saved to Gallery", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
                builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });
                builder.create().show();
            }
        });
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    public void paintClicked(View v){
        if(v != currpaint) {
            ImageButton view = (ImageButton) v;
            String color = view.getTag().toString();
            drawingView.setPaintColor(color);
            view.setImageDrawable(getResources().getDrawable(R.drawable.paint_pressed));
            currpaint.setImageDrawable(getResources().getDrawable(R.drawable.paint_normal));
            currpaint = (ImageButton) v;
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
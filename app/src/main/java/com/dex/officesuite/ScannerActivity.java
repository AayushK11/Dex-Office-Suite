package com.dex.officesuite;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NavUtils;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;

import android.Manifest;
import android.content.ActivityNotFoundException;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.pdf.PdfDocument;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.github.barteksc.pdfviewer.PDFView;
import com.github.barteksc.pdfviewer.util.FitPolicy;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

public class ScannerActivity extends AppCompatActivity {

    private static final int SEND_CAMERA_IMAGE_CODE = 101 ;
    private static final int CAMERA_PERM_CODE = 102;
    private static final int PIC_CROP = 103 ;
    private static final int READ_PERM_CODE = 104;
    private static final int WRITE_PERM_CODE = 105;

    PDFView pdfView;
    Button startScan;
    ConstraintLayout Backdrop;
    private static final int GALLERY_CODE = 100;
    String imageFilePath;
    File image;
    Uri photoURI, picUri;
    Bitmap bitmap;
    ArrayList<Bitmap> bitlist = new ArrayList<>();
    String selected = "";
    SharedPreferences theme = null;

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
        setContentView(R.layout.activity_scanner);

        pdfView = findViewById(R.id.pdfView);
        startScan = findViewById(R.id.scan_multi);
        Backdrop = findViewById(R.id.layout_2);
        pdfView = findViewById(R.id.pdfView);

        startScan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selected = "Many";
                ImagePicker();
            }
        });
    }

    public void ImagePicker(){
        final CharSequence[] items = {"Take a Photo", "Select From Gallery", "Cancel"};

        AlertDialog.Builder builder = new AlertDialog.Builder(ScannerActivity.this);
        builder.setCancelable(true);
        builder.setItems(items, new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                if(items[which].equals("Take a Photo")){
                    dialog.dismiss();
                    askCameraPermissions();
                }
                else if(items[which].equals("Select From Gallery")){
                    dialog.dismiss();
                    Intent gallery = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                    startActivityForResult(gallery, GALLERY_CODE);
                }
                else if(items[which].equals("Cancel")){
                    dialog.dismiss();
                }
            }
        });
        builder.create().show();
    }

    private void askCameraPermissions(){
        if(ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(this, new String[] {Manifest.permission.CAMERA}, CAMERA_PERM_CODE);
        }
        else{
            askReadPermissions();
        }
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
            OpenCamera();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults){
        if(requestCode == CAMERA_PERM_CODE){
            if(grantResults.length > 0 && grantResults[0]==PackageManager.PERMISSION_GRANTED){
                askReadPermissions();
            }
            else {
                Toast.makeText(this,"Camera Permissions are required to capture images", Toast.LENGTH_SHORT).show();
            }
        }
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
                OpenCamera();
            }
            else {
                Toast.makeText(this,"R/W Permissions are required to ensure smooth functioning of the app", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void OpenCamera() {
        Intent pictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (pictureIntent.resolveActivity(getPackageManager()) != null) {
            File photoFile = null;
            try {
                photoFile = createImageFile();
            } catch (IOException ex) {
                ex.printStackTrace();
            }
            if (photoFile != null) {
                photoURI = FileProvider.getUriForFile(this, "com.dex.officesuite.provider", photoFile);
                pictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                startActivityForResult(pictureIntent, SEND_CAMERA_IMAGE_CODE);
            }
        }
    }

    public File createImageFile() throws IOException {
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date());
        String imageFileName = "IMG_" + timeStamp + "_";

        File root = new File(Environment.getExternalStorageDirectory(), "/Dex Office/Scanner/Temp");
        if(!root.exists()){
            //noinspection ResultOfMethodCallIgnored
            root.mkdirs();
        }


        image = File.createTempFile(
                imageFileName,
                ".jpg",
                root
        );
        imageFilePath = image.getAbsolutePath();
        return image;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == SEND_CAMERA_IMAGE_CODE && resultCode == RESULT_OK) {
            System.out.println(photoURI);
            photoURI = FileProvider.getUriForFile(getApplicationContext(), getApplicationContext().getPackageName() + ".provider", image);
            System.out.println(photoURI);
            cropCamera(photoURI);
        }
        if (requestCode == GALLERY_CODE && resultCode == RESULT_OK) {
            assert data != null;
            picUri = data.getData();
            System.out.println(picUri);
            cropGallery(picUri);
        }
        if (requestCode == PIC_CROP && resultCode == RESULT_OK) {
            assert data != null;
            picUri = data.getData();
            try {
                bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), picUri);
            } catch (IOException e) {
                e.printStackTrace();
            }
            if(selected.equals("Many")) {
                bitlist.add(bitmap);
                AlertDialog.Builder builder = new AlertDialog.Builder(this,R.style.AlertDialogCustom);
                builder.setTitle("Add More?");
                builder.setMessage("Do you want to add more images");
                builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        ImagePicker();
                    }
                });
                builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        makePDFMulti(bitlist);
                    }
                });
                builder.create().show();
            }
        }
    }

    public void cropCamera(Uri photoURI){
        this.grantUriPermission("com.android.camera",photoURI, Intent.FLAG_GRANT_WRITE_URI_PERMISSION | Intent.FLAG_GRANT_READ_URI_PERMISSION);

        Intent intent = new Intent("com.android.camera.action.CROP");
        intent.setDataAndType(photoURI, "image/*");

        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        intent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION);

        intent.putExtra("crop", "true");
        intent.putExtra("return-data", true);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
        startActivityForResult(intent, PIC_CROP);
    }

    public void cropGallery(Uri picUri){
        try {
            Intent cropIntent = new Intent("com.android.camera.action.CROP");
            cropIntent.setDataAndType(picUri, "image/*");
            cropIntent.putExtra("return-data", true);
            cropIntent.putExtra("crop", "true");
            cropIntent.putExtra(MediaStore.EXTRA_OUTPUT, picUri);
            startActivityForResult(cropIntent, PIC_CROP);
        }
        catch(ActivityNotFoundException anfe){
            //display an error message
            String errorMessage = "Whoops - your device doesn't support the crop action!";
            Toast toast = Toast.makeText(this, errorMessage, Toast.LENGTH_SHORT);
            toast.show();
        }
    }

    public void makePDFMulti(ArrayList<Bitmap> bitlist){
        ProgressBar progressBar = findViewById(R.id.simpleProgressBar);
        TextView loading = findViewById(R.id.loading);
        progressBar.setVisibility(View.VISIBLE);
        loading.setVisibility(View.VISIBLE);
        Backdrop.setVisibility(View.INVISIBLE);
        pdfView.setVisibility(View.VISIBLE);
        TextView guide = findViewById(R.id.guide1_tv);
        guide.setVisibility(View.VISIBLE);

        PdfDocument pdfDocument = new PdfDocument();
        int i=0;

        for(Bitmap bitmap: bitlist){
            PdfDocument.PageInfo pi = new PdfDocument.PageInfo.Builder(bitmap.getWidth(),bitmap.getHeight(),i+1).create();

            PdfDocument.Page page = pdfDocument.startPage(pi);
            Canvas canvas = page.getCanvas();
            Paint paint = new Paint();
            paint.setColor(Color.parseColor("#FFFFFF"));
            canvas.drawPaint(paint);

            bitmap = Bitmap.createScaledBitmap(bitmap, bitmap.getWidth(), bitmap.getHeight(), true);
            paint.setColor(Color.BLUE);
            canvas.drawBitmap(bitmap,0,0, null);
            pdfDocument.finishPage(page);
        }

        File root = new File(Environment.getExternalStorageDirectory(), "/Dex Office/Scanner");
        System.out.println(root);
        if(!root.exists()){
            //noinspection ResultOfMethodCallIgnored
            root.mkdirs();
        }
        String timeStamp = SimpleDateFormat.getDateTimeInstance().format(new Date());
        timeStamp = "PDF_"+timeStamp+".pdf";
        File file = new File(root, timeStamp);
        System.out.println(file.toString());

        try {
            FileOutputStream fileOutputStream = new FileOutputStream(file);
            pdfDocument.writeTo(fileOutputStream);
        }catch (IOException e){
            e.printStackTrace();
        }

        pdfDocument.close();

        Uri PDFUri = Uri.fromFile(file);
        pdfView.fromUri(PDFUri)
                .enableSwipe(true) // allows to block changing pages using swipe
                .swipeHorizontal(true)
                .enableDoubletap(true)
                .defaultPage(0)
                .enableAnnotationRendering(false) // render annotations (such as comments, colors or forms)
                .password(null)
                .scrollHandle(null)
                .enableAntialiasing(true)
                .spacing(0)
                .pageFitPolicy(FitPolicy.BOTH)
                .load();

        progressBar.setVisibility(View.GONE);
        loading.setVisibility(View.GONE);


        AlertDialog.Builder builder = new AlertDialog.Builder(this,R.style.AlertDialogCustom);
        builder.setCancelable(true);
        builder.setTitle("PDF CREATED");
        builder.setMessage("Please Check Your File Directory for the pdf\n\n" +file.toString());
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                dialog.dismiss();
            }
        });
        builder.create().show();
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
package com.dex.officesuite;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NavUtils;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;

import android.Manifest;
import android.content.ActivityNotFoundException;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.ml.vision.FirebaseVision;
import com.google.firebase.ml.vision.common.FirebaseVisionImage;
import com.google.firebase.ml.vision.text.FirebaseVisionText;
import com.google.firebase.ml.vision.text.FirebaseVisionTextRecognizer;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class OcrActivity extends AppCompatActivity {

    public static final int CAMERA_PERM_CODE = 101;
    public static final int SEND_CAMERA_IMAGE_CODE = 102;
    public static final int GALLERY_CODE = 103;
    private static final int PIC_CROP = 104 ;
    private static final int READ_PERM_CODE = 105;
    private static final int WRITE_PERM_CODE = 106;


    ImageView preview_IM;
    Button chooseImage_BT, scanNow_BT, copy_BT;
    ScrollView scrollView;
    TextView guide_TV, guide2_TV, outputText_TV;
    Uri picUri, photoURI;
    Bitmap bitmap;
    FirebaseVisionImage fbvImage;
    SharedPreferences theme = null;
    String imageFilePath;
    File image;

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
        setContentView(R.layout.activity_ocr);

        preview_IM = findViewById(R.id.imageView);
        chooseImage_BT = findViewById(R.id.chooseImageButton);
        scanNow_BT = findViewById(R.id.scanNow);
        copy_BT = findViewById(R.id.copyButton);
        guide_TV = findViewById(R.id.text_guide);
        guide2_TV = findViewById(R.id.detectTextGuide);
        outputText_TV = findViewById(R.id.outputText);
        scrollView = findViewById(R.id.ScrollView);

        chooseImage_BT.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                guide2_TV.setText(R.string.text_guide_3);
                scrollView.setVisibility(View.INVISIBLE);
                preview_IM.setVisibility(View.INVISIBLE);
                ImagePicker();
            }
        });

        scanNow_BT.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (guide2_TV.getVisibility() == View.INVISIBLE) {
                    Toast toast = Toast.makeText(getApplicationContext(), "Please choose an image first", Toast.LENGTH_SHORT);
                    toast.show();
                } else {
                    guide2_TV.setText(R.string.text_guide_4);
                    analyse();
                }
            }
        });

        preview_IM.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ByteArrayOutputStream stream = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
                byte[] byteArray = stream.toByteArray();

                Intent in1 = new Intent(OcrActivity.this, ImageFullScreenActivity.class);
                in1.putExtra("image",byteArray);
                startActivity(in1);
                try {
                    stream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });

    }

    public void ImagePicker(){
        final CharSequence[] items = {"Take a Photo", "Select From Gallery", "Cancel"};

        AlertDialog.Builder builder = new AlertDialog.Builder(OcrActivity.this);
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

        File root = new File(Environment.getExternalStorageDirectory(), "/Dex Office/OCR");
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
        if (requestCode == SEND_CAMERA_IMAGE_CODE && resultCode==RESULT_OK) {
            photoURI = FileProvider.getUriForFile(getApplicationContext(), getApplicationContext().getPackageName()+".provider", image);
            cropCamera(photoURI);
        }
        if(requestCode == GALLERY_CODE && resultCode==RESULT_OK){
            assert data != null;
            picUri = data.getData();
            cropGallery(picUri);
        }
        if(requestCode == PIC_CROP && resultCode==RESULT_OK){
            assert data != null;
            picUri = data.getData();
            try {
                bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(),picUri);
            } catch (IOException e) {
                e.printStackTrace();
            }
            preview_IM.setVisibility(View.VISIBLE);
            preview_IM.setImageBitmap(bitmap);
            guide_TV.setText(R.string.text_guide_2);
            guide2_TV.setVisibility(View.VISIBLE);
            scanNow_BT.setVisibility(View.VISIBLE);
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

    public void analyse() {
        fbvImage = FirebaseVisionImage.fromBitmap(bitmap);
        FirebaseVisionTextRecognizer detector = FirebaseVision.getInstance().getOnDeviceTextRecognizer();

        detector.processImage(fbvImage)
                .addOnSuccessListener(new OnSuccessListener<FirebaseVisionText>() {
                    @Override
                    public void onSuccess(FirebaseVisionText firebaseVisionText) {
                        process(firebaseVisionText);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {                                                //If Failure, add Listener
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(getApplicationContext(), "Error", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    public void process(FirebaseVisionText text){

        scrollView.setVisibility(View.VISIBLE);
        outputText_TV.setVisibility(View.VISIBLE);
        copy_BT.setVisibility(View.VISIBLE);

        outputText_TV.setText(null);

        if(text.getTextBlocks().size() == 0){
            outputText_TV.setText(R.string.no_text);
        }
        for(FirebaseVisionText.TextBlock block : text.getTextBlocks()){
            outputText_TV.append(block.getText());
            outputText_TV.append("\n\n");
        }

        copy_BT.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
                ClipData clip = ClipData.newPlainText("EditText", outputText_TV.getText().toString());
                assert clipboard != null;
                clipboard.setPrimaryClip(clip);
                Toast.makeText(OcrActivity.this, "Text Copied", Toast.LENGTH_SHORT).show();
            }
        });
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


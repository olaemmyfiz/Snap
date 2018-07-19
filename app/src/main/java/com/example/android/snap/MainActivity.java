package com.example.android.snap;

import android.Manifest;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.provider.MediaStore;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Base64;
import android.view.View;
import android.widget.*;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;

public class MainActivity extends AppCompatActivity {

    Button GetImageButton, UploadToServer;
    ImageView ShowSelectedImage;
    EditText imageName;
    Bitmap FixBitmap;

    String ImageTag = "image_tag";
    String ImageName = "image_data";
    ProgressDialog progressDialog;
    ByteArrayOutputStream byteArrayOutputStream;
    byte[] byteArray;
    String ConvertImage;
    String GetImageNameFromEditText;

    HttpURLConnection httpURLConnection;
    URL url;
    OutputStream outputStream;
    BufferedWriter bufferedWriter;
    int RC;
    BufferedReader bufferedReader;
    StringBuilder stringBuilder;
    boolean check = true;
    private int GALLERY = 1, CAMERA = 2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        GetImageButton = (Button)findViewById(R.id.buttonSelect);
        UploadToServer = (Button)findViewById(R.id.buttonUpload);
        ShowSelectedImage = (ImageView)findViewById(R.id.imageView);
        imageName = (EditText)findViewById(R.id.imageName);
        byteArrayOutputStream = new ByteArrayOutputStream();
        GetImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showPictureDialog();
            }
        });

        UploadToServer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                GetImageNameFromEditText = imageName.getText().toString();
                UploadImageToSever();
            }
        });

        if(ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.CAMERA) !=
                PackageManager.PERMISSION_GRANTED){
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    requestPermissions(new String[]{Manifest.permission.CAMERA},5);
                }

        }
    }

    private void showPictureDialog(){
        AlertDialog.Builder pictureDialog = new AlertDialog.Builder(this);
        pictureDialog.setTitle("Selection Action");
        String[] pictureDialogItems = {"Gallery","Camera"};
        pictureDialog.setItems(pictureDialogItems, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                switch (i){
                    case 0: choosePhotoFromGallrey();
                                break;
                    case 1: takePhotFromGallrey();
                                break;
                }
            }
        });
        pictureDialog.show();

    }

    public void choosePhotoFromGallrey(){
        Intent galleryIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(galleryIntent,GALLERY);
    }

    private  void takePhotFromGallrey(){
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(intent,CAMERA);
    }
//NOTE HERE.
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode == this.RESULT_CANCELED){
            return;
        }

        if(requestCode == GALLERY){
            if(data !=null){
                Uri contentURI =data.getData();

                try {
                    FixBitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(),contentURI);
                    Toast.makeText(this, "Image Saved", Toast.LENGTH_SHORT).show();
                    ShowSelectedImage.setImageBitmap(FixBitmap);
                    UploadToServer.setVisibility(View.VISIBLE);
                } catch (IOException e) {
                    e.printStackTrace();
                    Toast.makeText(this, "Failed", Toast.LENGTH_SHORT).show();
                }
            }
        }

        else if(requestCode == CAMERA){

            FixBitmap = (Bitmap) data.getExtras().get("data");
            ShowSelectedImage.setImageBitmap(FixBitmap);
            UploadToServer.setVisibility(View.VISIBLE);

        }
    }

    public void UploadImageToSever(){
        FixBitmap.compress(Bitmap.CompressFormat.PNG,40,byteArrayOutputStream);
        byteArray =  byteArrayOutputStream.toByteArray();
        ConvertImage = Base64.encodeToString(byteArray,Base64.DEFAULT);

        class AsyncTaskUploadClass extends AsyncTask<Void,Void,String>{

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                progressDialog = ProgressDialog.show(MainActivity.this,"Image Uploading","Please Wait", false,false);
                }

            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);
                progressDialog.dismiss();
                Toast.makeText(MainActivity.this,s, Toast.LENGTH_SHORT).show();
            }

            @Override
            protected String doInBackground(Void... params) {

                ImageProcessClass imageProcessClass = new ImageProcessClass();
                HashMap<String,String> HashMapParams = new HashMap<String, String>();
                HashMapParams.put(ImageTag, GetImageNameFromEditText);
                HashMapParams.put(ImageName,ConvertImage);

                String FinalData = imageProcessClass.ImageHttpRequest("Write to address later", HashMapParams);
                return FinalData;
            }
        }

        AsyncTaskUploadClass AsynctaskUploadClassOBJ = new AsyncTaskUploadClass();
        AsynctaskUploadClassOBJ.execute();
    }
}

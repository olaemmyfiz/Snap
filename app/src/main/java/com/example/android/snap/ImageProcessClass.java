package com.example.android.snap;

import android.content.pm.PackageManager;
import android.support.annotation.NonNull;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;

public class ImageProcessClass extends MainActivity {
    /**HttpURLConnection httpURLConnection;
    URL url;
    OutputStream outputStream;
    BufferedWriter bufferedWriter;
    int RC;
    BufferedReader bufferedReader;
    StringBuilder stringBuilder;
    boolean check = true;
     */

    public String ImageHttpRequest(String requestURL, HashMap<String,String> PData){

        StringBuilder stringBuilder = new StringBuilder();

        try {
            url = new URL(requestURL);
            httpURLConnection = (HttpURLConnection) url.openConnection();
            httpURLConnection.setReadTimeout(20000);
            httpURLConnection.setConnectTimeout(20000);
            httpURLConnection.setRequestMethod("POST");
            httpURLConnection.setDoInput(true);
            httpURLConnection.setDoOutput(true);
            outputStream = httpURLConnection.getOutputStream();
            bufferedWriter = new BufferedWriter( new OutputStreamWriter(outputStream,"UTF-8"));
            bufferedWriter.write(bufferedWriterDataFN(PData));
            bufferedWriter.flush();
            bufferedWriter.close();
            outputStream.close();
            RC = httpURLConnection.getResponseCode();

            if(RC == HttpURLConnection.HTTP_OK){
                bufferedReader = new BufferedReader(new InputStreamReader(httpURLConnection.getInputStream()));
                stringBuilder = new StringBuilder();

                String RC2;

                while ((RC2 = bufferedReader.readLine()) !=null){
                    stringBuilder.append(RC2);
                }
            }

        }
        catch (Exception e) {
            e.printStackTrace();
        }
        return stringBuilder.toString();
    }

    private String bufferedWriterDataFN(HashMap<String,String> HashMapParams) throws UnsupportedEncodingException{

        stringBuilder = new StringBuilder();

        for (Map.Entry<String,String> KEY : HashMapParams.entrySet()){
            if(check)
                check=false;
            else
                stringBuilder.append("&amp;");
                stringBuilder.append(URLEncoder.encode((KEY.getKey()),"UTF-8"));
        }
        return stringBuilder.toString();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,  String[] permissions,  int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(requestCode==5){
            if(grantResults[0] == PackageManager.PERMISSION_GRANTED){
                //You are Good to use the Camera Now
            }

            else {
                Toast.makeText(this, "Access to Camera Denied", Toast.LENGTH_SHORT).show();
            }
        }
    }
}

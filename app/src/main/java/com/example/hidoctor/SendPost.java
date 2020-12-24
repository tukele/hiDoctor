package com.example.hidoctor;

import android.os.AsyncTask;

import java.io.IOException;

import okhttp3.Headers;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class SendPost extends AsyncTask {
    private String URL;
    private final OkHttpClient client = new OkHttpClient();
    String email;
    String password;
    boolean flag=false;
    String JSON;

    public SendPost(String URL, String email, String password) {
        this.URL = URL;
        this.email = email;
        this.password = password;
    }

    public boolean getFlag(){
        return flag;
    }
    public String getJSON(){
        return JSON;
    }

    @Override
    protected Object doInBackground(Object[] objects) {
        RequestBody requestBody = new MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("email", email)
                .addFormDataPart("password", password)
                .build();

        Request request = new Request.Builder()
                .url(URL)
                .post(requestBody)
                .build();


        try (Response response = client.newCall(request).execute()) {
            if (!response.isSuccessful()) throw new IOException("Unexpected code " + response);
            Headers responseHeaders = response.headers();
            for (int i = 0; i < responseHeaders.size(); i++) {
                System.out.println(responseHeaders.name(i) + ": " + responseHeaders.value(i));
            }
            JSON=response.body().string();
            flag=!(JSON.equals("no"));
            return null;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}

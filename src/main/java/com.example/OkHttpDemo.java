package com.example;

import okhttp3.*;

import java.io.IOException;

public class demo {

    public void getHttp(){
       try {
           OkHttpClient client = new OkHttpClient().newBuilder()
                   .build();
           MediaType mediaType = MediaType.parse("text/plain");
           RequestBody body = RequestBody.create(mediaType, "");
           Request request = new Request.Builder()
                   .url("api-test.micoworld.net/admin/invalid/clean_device?did=7a847fc3324a093c160b87934650fb7b26da9ac5")
                   .method("GET", body)
                   .addHeader("did", "5up3rp0w3r")
                   .build();
           Response response = client.newCall(request).execute();
       }catch() {}
    }




}

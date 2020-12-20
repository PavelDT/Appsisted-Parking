package com.github.paveldt.appsistedparking.util;

import java.io.IOException;

import okhttp3.FormBody;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class WebRequestManager implements Runnable {
    private volatile String result;
    private final OkHttpClient httpClient = new OkHttpClient();

    public WebRequestManager() {


    }

    public void post() {

    }

    @Override
    public void run() {
        result = get();
    }

    public String getResult() {
        return result;
    }

    private String get() {

        HttpUrl.Builder urlBuilder = HttpUrl.parse("http://10.0.2.2").newBuilder();
        urlBuilder.port(8080);
        urlBuilder.addPathSegment("user");
        urlBuilder.addPathSegment("exists");
        urlBuilder.addQueryParameter("username", "root");
        urlBuilder.addQueryParameter("password", "password");
        String url = urlBuilder.build().toString();

        Request request = new Request.Builder().url(url)
                                               .get()
                                               .build();

        try (Response response = httpClient.newCall(request).execute()) {

            if (!response.isSuccessful()) {
                throw new IOException("BAD GET - Unexpected code " + response);
            }

            // Get response body
            String ans = response.body().string();
            System.out.println("DEBUG ---- " + ans);
            return ans;
        } catch (IOException ex) {
            // log the exception
            ex.printStackTrace();
            return "error";
        }
    }
}

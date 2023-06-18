
package com.mico.common.util;

import lombok.extern.slf4j.Slf4j;
import okhttp3.*;

import javax.net.ssl.*;
import java.io.IOException;
import java.security.SecureRandom;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.TimeUnit;

/**
 * 封装okhttp客户端
 *
 * @author : yuguo
 * @date: 2021/5/31 4:46 下午
 */

@Slf4j
public class OkHttpClientUtil {

    private final OkHttpClient client;
    private static volatile OkHttpClientUtil instance;
    public static final MediaType JSON = MediaType.get("application/json; charset=utf-8");

    private static final int TIME_OUT = 8000;

    private OkHttpClientUtil() {
        OkHttpClient.Builder okHttpBuilder = new OkHttpClient.Builder();
        client = okHttpBuilder
                .readTimeout(TIME_OUT, TimeUnit.MILLISECONDS)
                .connectTimeout(TIME_OUT, TimeUnit.MILLISECONDS)
                .writeTimeout(TIME_OUT, TimeUnit.MILLISECONDS)
                // 错误重连
                .retryOnConnectionFailure(true)
                .hostnameVerifier(new TrustAllHostnameVerifier())
                // 这里使用暴力方式 信任任何机构的证书
                .sslSocketFactory(createSSLSocketFactory(), new TrustAllCerts())
                .build();
    }

    public static OkHttpClientUtil getInstance() {
        if (instance == null) {
            synchronized (OkHttpClientUtil.class) {
                if (instance == null) {
                    instance = new OkHttpClientUtil();
                }
            }
        }
        return instance;
    }

    public Response get(String url) {
        Request request = new Request.Builder()
                .url(url)
                .build();
        try {
            return client.newCall(request).execute();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public Response get(String url, Map<String, String> headers) {
        Request request = new Request.Builder()
                .url(url)
                .headers(Headers.of(headers))
                .build();
        try {
            return client.newCall(request).execute();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public Response get(String url, Map<String, String> headers, Map<String, String> params) {
        Request.Builder reqBuild = new Request.Builder();
        HttpUrl.Builder urlBuilder = Objects.requireNonNull(HttpUrl.parse(url)).newBuilder();
        params.forEach(urlBuilder::addQueryParameter);
        Request request = reqBuild.headers(Headers.of(headers)).url(urlBuilder.toString()).build();
        try {
            return client.newCall(request).execute();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public Response post(String url, String jsonBody) {
        return this.post(url, null, jsonBody);
    }

    public Response post(String url, Map<String, String> headers) {
        return this.post(url, headers, "");
    }

    public Response post(String url, Map<String, String> headers, RequestBody body) {
        Request request = new Request.Builder()
                .url(url)
                .post(body)
                .headers(Headers.of(headers))
                .build();
        try {
            return client.newCall(request).execute();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public Response post(String url, Map<String, String> headers, Map<String, String> params) {

        FormBody.Builder formBodyBuilder = new FormBody.Builder();
        Set<String> keySet = params.keySet();
        for (String key : keySet) {
            String value = params.get(key);
            formBodyBuilder.add(key, value);
        }

        FormBody formBody = formBodyBuilder.build();

        Request request;
        if (headers != null) {
            request = new Request.Builder()
                    .url(url)
                    .post(formBody)
                    .headers(Headers.of(headers))
                    .build();
        } else {
            request = new Request.Builder()
                    .url(url)
                    .post(formBody)
                    .build();
        }

        try {
            return client.newCall(request).execute();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public Response post(String url, Map<String, String> headers, String jsonBody) {
        RequestBody body = RequestBody.create(JSON, jsonBody);
        Request request = new Request.Builder()
                .url(url)
                .post(body)
                .headers(Headers.of(headers == null ? new HashMap<>() : headers))
                .build();
        try {
            return client.newCall(request).execute();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static SSLSocketFactory createSSLSocketFactory() {
        SSLSocketFactory ssfFactory = null;
        try {
            SSLContext sc = SSLContext.getInstance("TLS");
            sc.init(null, new TrustManager[]{new TrustAllCerts()}, new SecureRandom());
            ssfFactory = sc.getSocketFactory();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return ssfFactory;
    }

    private static class TrustAllCerts implements X509TrustManager {
        public void checkClientTrusted(X509Certificate[] chain, String authType) throws CertificateException {
        }

        public void checkServerTrusted(X509Certificate[] chain, String authType) throws CertificateException {
        }

        public X509Certificate[] getAcceptedIssuers() {
            return new X509Certificate[0];
        }
    }

    private static class TrustAllHostnameVerifier implements HostnameVerifier {
        public boolean verify(String hostname, SSLSession session) {
            return true;
        }
    }


}

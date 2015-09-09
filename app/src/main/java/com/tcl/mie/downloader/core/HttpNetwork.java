package com.tcl.mie.downloader.core;

import com.squareup.okhttp.Call;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.ProtocolVersion;
import org.apache.http.StatusLine;
import org.apache.http.entity.BasicHttpEntity;
import org.apache.http.message.BasicHeader;
import org.apache.http.message.BasicHttpResponse;
import org.apache.http.message.BasicStatusLine;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLSocketFactory;

/**
 * referenced from volley
 * Created by difei.zou on 2015/6/15.
 */
public class HttpNetwork {

    private static final long timeoutMs = 30000;
    private static OkHttpClient client;

    static {
        client = new OkHttpClient();
        client.setConnectTimeout(timeoutMs, TimeUnit.SECONDS);
    }

    public HttpNetwork() {

    }

    public static Call connect(String url, Map<String, String> additionalHeaders, String method) throws IOException {
        Request.Builder requestBuilder = new Request.Builder();
        if( additionalHeaders != null) {
            for (String headerName : additionalHeaders.keySet()) {
                requestBuilder.addHeader(headerName, additionalHeaders.get(headerName));
            }
        }
        requestBuilder.url(url);
        requestBuilder.method(method, null);
        Request request = requestBuilder.build();
        Call call = client.newCall(request);
        return call;
    }
}

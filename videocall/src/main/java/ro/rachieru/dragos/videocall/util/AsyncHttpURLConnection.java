/*
 *  Copyright 2015 The WebRTC Project Authors. All rights reserved.
 *
 *  Use of this source code is governed by a BSD-style license
 *  that can be found in the LICENSE file in the root of the source
 *  tree. An additional intellectual property rights grant can be found
 *  in the file PATENTS.  All contributing project authors may
 *  be found in the AUTHORS file in the root of the source tree.
 */

package ro.rachieru.dragos.videocall.util;

import androidx.annotation.Nullable;
import okhttp3.*;
import ro.rachierudragos.upitapi.OkHttpClientHelperKt;

import java.io.IOException;
import java.net.SocketTimeoutException;
import java.util.concurrent.TimeUnit;

/**
 * Asynchronous http requests implementation.
 */
public class AsyncHttpURLConnection {
    private static final int HTTP_TIMEOUT_MS = 8000;
    //  private static final String HTTP_ORIGIN = "https://appr.tc";
    private static final String HTTP_ORIGIN = "https://amiss-25454.appspot.com";
    private final String method;
    private final String url;
    private final String message;
    private final AsyncHttpEvents events;
    @Nullable
    private String contentType;
    private OkHttpClient client;

    /**
     * Http requests callbacks.
     */
    public interface AsyncHttpEvents {
        void onHttpError(String errorMessage);

        void onHttpComplete(String response);
    }

    public AsyncHttpURLConnection(String method, String url, String message, AsyncHttpEvents events) {
        this.method = method;
        this.url = url;
        this.message = message;
        this.events = events;
        client = OkHttpClientHelperKt.getUnsafeOkHttpClientBuilder()
                .connectTimeout(HTTP_TIMEOUT_MS, TimeUnit.MILLISECONDS)
                .readTimeout(HTTP_TIMEOUT_MS, TimeUnit.MILLISECONDS)
                .build();
    }

    public void setContentType(String contentType) {
        this.contentType = contentType;
    }

    public void send() {
        new Thread(this::sendHttpMessage).start();
    }

    private void sendHttpMessage() {
        RequestBody body;
        String mediaType;
        if (contentType == null) {
            mediaType = "text/plain; charset=utf-8";
        } else {
            mediaType = contentType;
        }
        if (message != null) {
            body = RequestBody.create(MediaType.parse(mediaType), message);
        } else {
            body = RequestBody.create(MediaType.parse(mediaType), new byte[0]);
        }
        Request.Builder requestBuilder = new Request.Builder()
                .url(url)
                .method(method, body)
                .addHeader("origin", HTTP_ORIGIN);
//            connection.setUseCaches(false);
        // TODO(glaznev) - query request origin from pref_room_server_url_key preferences.

        client.newCall(requestBuilder.build()).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                if (e instanceof SocketTimeoutException) {
                    events.onHttpError("HTTP " + method + " to " + url + " timeout");
                } else
                    events.onHttpError("HTTP " + method + " to " + url + " error: " + e.getMessage());
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                // Get response.
                int responseCode = response.code();
                if (responseCode != 200) {
                    events.onHttpError("Non-200 response to " + method + " to URL: " + url + " : "
                            + response.headers());
                    return;
                }
                events.onHttpComplete(response.body().string());
            }
        });
    }

}

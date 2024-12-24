package io.github.buzzxu.spuddy.util;

import okhttp3.ConnectionPool;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.RequestBody;
import okhttp3.internal.Util;
import okio.BufferedSink;
import okio.Okio;
import okio.Source;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import java.io.IOException;
import java.io.InputStream;
import java.security.SecureRandom;
import java.security.cert.X509Certificate;
import java.util.concurrent.TimeUnit;

public enum OkHttps {

    HTTP(http()),HTTPS(https());
    private OkHttpClient client;

    OkHttps(OkHttpClient client) {
        this.client = client;
    }

    public OkHttpClient client() {
        return client;
    }


    public static OkHttpClient of(){
        return HTTP.client();
    }

    public static OkHttpClient ofSSL(){
        return HTTPS.client();
    }

    public static OkHttpClient http(){
        return new OkHttpClient().newBuilder()
                .connectTimeout(10, TimeUnit.SECONDS)
                .writeTimeout(10, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS)
                .connectionPool(new ConnectionPool(20, 20, TimeUnit.MINUTES))
                .build();
    }
    public static OkHttpClient https(){
        X509TrustManager xtm = new X509TrustManager() {
            @Override
            public void checkClientTrusted(X509Certificate[] chain, String authType) {
            }

            @Override
            public void checkServerTrusted(X509Certificate[] chain, String authType) {
            }

            @Override
            public X509Certificate[] getAcceptedIssuers() {
                return new X509Certificate[0];
            }
        };
        SSLContext sslContext = null;
        try {
            sslContext = SSLContext.getInstance("SSL");
            sslContext.init(null, new TrustManager[]{xtm}, new SecureRandom());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return new OkHttpClient.Builder()
                .sslSocketFactory(sslContext.getSocketFactory(),xtm)
                .hostnameVerifier((hostname, session) -> true)
                .connectTimeout(10L, TimeUnit.SECONDS)
                .writeTimeout(10L, TimeUnit.SECONDS)
                .readTimeout(30L, TimeUnit.SECONDS)
                .connectionPool(new ConnectionPool(20, 20L, TimeUnit.MINUTES))
                .build();
    }




    public static RequestBody createBinaryBody(final MediaType mediaType, final InputStream inputStream) {
        if (inputStream == null) throw new NullPointerException("image == null");
        return new BinaryRequestBody(mediaType, inputStream);
    }


    private static class BinaryRequestBody extends RequestBody{
        private final MediaType mediaType;
        private final InputStream inputStream;

        private BinaryRequestBody(MediaType mediaType, InputStream inputStream) {
            this.mediaType = mediaType;
            this.inputStream = inputStream;
        }

        @Override
        public MediaType contentType() {
            return mediaType;
        }
        @Override
        public long contentLength() {
            try {
                return inputStream.available();
            } catch (IOException e) {
                return 0;
            }
        }
        @Override
        public void writeTo(BufferedSink sink) throws IOException {
            Source source = null;
            try {
                source = Okio.source(inputStream);
                sink.writeAll(source);
            } finally {
                Util.closeQuietly(source);
            }
        }
    }

}

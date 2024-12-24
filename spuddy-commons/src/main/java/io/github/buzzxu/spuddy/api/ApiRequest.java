package io.github.buzzxu.spuddy.api;

import io.github.buzzxu.spuddy.jackson.Jackson;
import io.github.buzzxu.spuddy.util.OkHttps;
import lombok.extern.slf4j.Slf4j;
import okhttp3.*;

import java.io.IOException;
import java.util.Map;
import java.util.function.Consumer;

import static org.springframework.util.MimeTypeUtils.APPLICATION_JSON;
import static org.springframework.util.MimeTypeUtils.APPLICATION_JSON_VALUE;

/**
 * @program: 
 * @description:
 * @author: xux
 * @create: 2021-08-06 09:10
 **/
@Slf4j
public enum ApiRequest {
    INSTANCE(new RequestCaller(OkHttps.of()));

    private RequestCaller caller;

    ApiRequest(RequestCaller caller) {
        this.caller = caller;
    }

    public <T> T post(String url, Map<String, String> headers, Consumer<FormBody.Builder> formData, Class<T> clazz) {
        FormBody.Builder form = new FormBody.Builder();
        formData.accept(form);
        return post(url, headers, form.build(), clazz);
    }

    public <T> T post(String url, Map<String, String> headers, Consumer<FormBody.Builder> formData, Class<T> clazz, Class<?>... parametrized) {
        FormBody.Builder form = new FormBody.Builder();
        formData.accept(form);
        return post(url, headers, form.build(), clazz, parametrized);
    }

    public <K, V> Map<K, V> post(String url, Map<String, String> headers, Consumer<FormBody.Builder> formData, Class<? extends Map> mapClass, Class<K> kClazz, Class<V> vClazz) {
        FormBody.Builder form = new FormBody.Builder();
        formData.accept(form);
        return post(url, headers, form.build(), mapClass, kClazz, vClazz);
    }

    public <T> T post(String url, Map<String, String> headers, Object data, Class<T> clazz) {
        return post(url, headers, RequestBody.create(Jackson.object2Json(data), MediaType.parse(APPLICATION_JSON_VALUE)), clazz);
    }

    public <T> T post(String url, Map<String, String> headers, Object data, Class<T> clazz, Class<?>... parametrized) {
        return post(url, headers, RequestBody.create(Jackson.object2Json(data), MediaType.parse(APPLICATION_JSON_VALUE)), clazz, parametrized);
    }

    public <K, V> Map<K, V> post(String url, Map<String, String> headers, Object data, Class<? extends Map> mapClass, Class<K> kClazz, Class<V> vClazz) {
        return post(url, headers, RequestBody.create(Jackson.object2Json(data), MediaType.parse(APPLICATION_JSON_VALUE)), mapClass, kClazz, vClazz);
    }

    public ResponseBody upload(String url, Map<String, String> headers, Consumer<MultipartBody.Builder> formData) {
        MultipartBody.Builder form = new MultipartBody.Builder()
                .setType(MultipartBody.FORM);
        formData.accept(form);
        return caller.execute(post(url, headers, form.build()));
    }
    public byte[] download(String url,Map<String,String> queryParams,Map<String, String> headers) throws IOException {
        return caller.bytes(get(url,queryParams,headers));
    }

    public <T> T post(String url, Map<String, String> headers, RequestBody body, Class<T> clazz) {
        return caller.execute(post(url, headers, body), clazz);
    }

    public <T> T post(String url, Map<String, String> headers, RequestBody body, Class<T> clazz, Class<?>... parametrized) {
        return caller.execute(post(url, headers, body), clazz, parametrized);
    }

    public <K, V> Map<K, V> post(String url, Map<String, String> headers, RequestBody body, Class<? extends Map> mapClass, Class<K> kClazz, Class<V> vClazz) {
        return caller.execute(post(url, headers, body), mapClass, kClazz, vClazz);
    }

    public ResponseBody post(String url, Map<String, String> headers, Object data) {
        return posts(url,headers,RequestBody.create(Jackson.object2Json(data), MediaType.parse(APPLICATION_JSON_VALUE)));
    }

    public ResponseBody posts(String url, Map<String, String> headers, Consumer<FormBody.Builder> formData) {
        FormBody.Builder form = new FormBody.Builder();
        formData.accept(form);
        return posts(url, headers, form.build());
    }

    public ResponseBody posts(String url, Map<String, String> headers, RequestBody body) {
        log.warn("Return body must contain try-with-resources!");
        return caller.execute(post(url, headers, body));
    }

    public Request.Builder post(String url, Map<String, String> headers, RequestBody body) {
        return reqBuilder(url, headers, req -> req.post(body));
    }


    public ResponseBody gets(String url,Map<String,String> queryParams,Map<String, String> headers) {
        log.warn("Return body must contain try-with-resources!");
        return caller.execute(get(url, queryParams, headers));
    }

    public Request.Builder get(String url, Map<String, String> headers) {
        return reqBuilder(url, headers, req -> req.get());
    }
    public Request.Builder get(String url,Map<String,String> queryParams,Map<String, String> headers){
        HttpUrl.Builder urlBuilder = HttpUrl.parse(url).newBuilder();
        if(queryParams != null && !queryParams.isEmpty()){
            queryParams.forEach((k,v)-> urlBuilder.addQueryParameter(k,v));
        }
        return reqBuilder(urlBuilder.build(), headers, req -> req.get());
    }

    public Request.Builder delete(String url, Map<String, String> headers) {
        return reqBuilder(url, headers, req -> req.delete());
    }

    public Request.Builder delete(String url, Map<String, String> headers, RequestBody body) {
        return reqBuilder(url, headers, req -> req.delete(body));
    }

    public Request.Builder put(String url, Map<String, String> headers, RequestBody body) {
        return reqBuilder(url, headers, req -> req.put(body));
    }

    private Request.Builder reqBuilder(String url, Map<String, String> headers, Consumer<Request.Builder> consumer) {
        Request.Builder builder = reqBuilder(headers);
        consumer.accept(builder.url(url));
        return builder;
    }
    private Request.Builder reqBuilder(HttpUrl url, Map<String, String> headers, Consumer<Request.Builder> consumer) {
        Request.Builder builder = reqBuilder(headers);
        consumer.accept(builder.url(url));
        return builder;
    }

    private Request.Builder reqBuilder(Map<String, String> headers){
        Request.Builder builder = new Request.Builder();
        if (headers != null && !headers.isEmpty()) {
            headers.forEach((k, v) -> builder.addHeader(k, v));
        }
        return builder;
    }
}

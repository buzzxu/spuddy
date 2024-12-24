package io.github.buzzxu.spuddy.api;


import io.github.buzzxu.spuddy.errors.ForbiddenException;
import io.github.buzzxu.spuddy.errors.NotFoundException;
import io.github.buzzxu.spuddy.exceptions.ApplicationException;
import io.github.buzzxu.spuddy.jackson.Jackson;
import lombok.extern.slf4j.Slf4j;
import okhttp3.*;

import java.io.IOException;
import java.util.Map;

@Slf4j
public class RequestCaller {

    private OkHttpClient httpClient;

    public RequestCaller(OkHttpClient httpClient) {
        this.httpClient = httpClient;
    }


    public <T> T execute(Request.Builder builder, Class<T> clazz, Class<?>... parametrized) {
        ResponseBody body = null;
        try {
            body = execute(builder);
            return Jackson.json2Object(body.bytes(), Jackson.buildType(clazz, parametrized));
        }catch (IOException ex){
            throw ApplicationException.raise(ex);
        }finally {
            if (body != null) {
                body.close();
            }
        }
    }

    public <K, V> Map<K, V> execute(Request.Builder builder, Class<? extends Map> mapClass, Class<K> kClazz, Class<V> vClazz) {
        ResponseBody body = null;
        try {
            body = execute(builder);
            return Jackson.json2Object(body.bytes(), Jackson.buildMapType(mapClass, kClazz, vClazz));
        } catch (IOException ex) {
            throw ApplicationException.raise(ex);
        }finally {
            if (body != null) {
                body.close();
            }
        }
    }

    public <T> T execute(Request.Builder builder, Class<T> clazz) {
        ResponseBody body = null;
        try {
            body = execute(builder);
            return Jackson.json2Object(body.bytes(), clazz);
        } catch (IOException ex) {
            throw ApplicationException.raise(ex);
        }finally {
            if (body != null) {
                body.close();
            }
        }
    }

    public byte[] bytes(Request.Builder builder) {
        ResponseBody body = null;
        try {
            body = execute(builder);
            return body.bytes();
        } catch (IOException ex) {
            throw ApplicationException.raise(ex);
        }finally {
            if (body != null) {
                body.close();
            }
        }
    }

    public Call call(Request request) {
        return httpClient.newCall(request);
    }

    public ResponseBody execute(Request.Builder builder) {
        return execute(builder.build());
    }

    public ResponseBody execute(Request request) {
        Call call = call(request);
        log.debug("caller method:{}, url:{}", call.request().method(), call.request().url());
        try {
            Response response = call.execute();
            log.info("caller code:{}, method:{}, url:{}", response.code(),call.request().method(),call.request().url());
            if (!response.isSuccessful()) {
                try {
                    switch (response.code()) {
                        case 401:
                        case 403:
                            throw new ForbiddenException("认证失败，拒绝响应").target(response);
                        case 404:
                            throw new NotFoundException("请求地址有误,请检查").target(response);
                        default:
                            throw ApplicationException.raise(response.message()).target(response);

                    }
                }finally {
                    //非成功也需要关闭
                    response.close();
                }
            }
            return response.body();
        } catch (IOException e) {
            throw ApplicationException.raise(e);
        } catch (ApplicationException ex) {
            throw ex;
        }
    }

    public OkHttpClient httpClient() {
        return httpClient;
    }
}

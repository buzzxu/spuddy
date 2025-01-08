package io.github.buzzxu.thirdparty.image;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.fasterxml.jackson.databind.JsonNode;
import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import io.github.buzzxu.spuddy.errors.ForbiddenException;
import io.github.buzzxu.spuddy.exceptions.ApplicationException;
import io.github.buzzxu.spuddy.jackson.Jackson;
import io.github.buzzxu.spuddy.util.OkHttps;
import io.github.buzzxu.spuddy.util.OnlyOnceCondition;
import io.github.buzzxu.spuddy.util.UploadFile;
import okhttp3.*;

import java.io.IOException;
import java.io.InputStream;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

import static com.google.common.base.Preconditions.checkArgument;
import static io.github.buzzxu.spuddy.util.OkHttps.createBinaryBody;

/**
 * @program: yuanmai-platform
 * @description: 自己的图片服务器
 * @author: 徐翔
 * @create: 2018-06-30 12:18
 **/
public class BuzzImager {


    private final String url;
    //密钥 (jwt)认证
    private final String token;
    private static final Map<String, String> CONTENT_TYPE = Maps.newHashMap();
    private static final OnlyOnceCondition once = OnlyOnceCondition.create("图片服务只能初始化一次");

    static {
        CONTENT_TYPE.put("jpg", "io/github/buzzxu/thirdparty/image/jpeg");
        CONTENT_TYPE.put("jpeg", "io/github/buzzxu/thirdparty/image/jpeg");
        CONTENT_TYPE.put("gif", "io/github/buzzxu/thirdparty/image/gif");
        CONTENT_TYPE.put("png", "io/github/buzzxu/thirdparty/image/png");
        CONTENT_TYPE.put("webp", "io/github/buzzxu/thirdparty/image/webp");
        CONTENT_TYPE.put("heic", "io/github/buzzxu/thirdparty/image/heic");
        CONTENT_TYPE.put("heif", "io/github/buzzxu/thirdparty/image/heif");
    }

    public BuzzImager(String url, String secret) {
        once.check();
        checkArgument(!Strings.isNullOrEmpty(url) ,"请设置图片服务器路径");
        checkArgument(!Strings.isNullOrEmpty(secret) ,"请设置图片服务器密钥");
        this.url = url;
        try {
            Algorithm algorithm = Algorithm.HMAC512(secret);
            token = "Bearer "+JWT.create()
                    .sign(algorithm);
        }catch (Exception ex){
            throw ApplicationException.raise(ex);
        }
    }

    /**
     *
     * @param folder    文件夹 e.g. /test
     * @param fileName  文件名
     * @param file      文件
     * @param consumer  参数
     * @return
     */
    public String upload(String folder,String fileName,InputStream file, Consumer<MultipartBody.Builder> consumer)throws ImageException{
        return upload(folder, null,consumer, UploadFile.of(fileName, file)).get(0);
    }

    public String upload(String folder,String fileName,byte[] content, Consumer<MultipartBody.Builder> consumer)throws ImageException{
        return upload(folder, null,consumer,UploadFile.of(fileName, content)).get(0);
    }
    public String upload(String folder,UploadFile file, Consumer<MultipartBody.Builder> consumer)throws ImageException{
        return upload(folder,null, consumer,file).get(0);
    }
    public List<String> upload(String folder,Consumer<MultipartBody.Builder> consumer,UploadFile... files)throws ImageException{
        return upload(folder,null, consumer,files);
    }
    public String upload(String folder,String base64, Consumer<MultipartBody.Builder> consumer)throws ImageException{
        return upload(folder,base64, consumer,new UploadFile[0]).get(0);
    }
    /**
     * 上传图片
     * @param folder    文件夹 e.g. /test
     * @param base64    base64
     * @param consumer  参数
     * @`param file      文件
     * @return
     */
    public List<String> upload(String folder, String base64,Consumer<MultipartBody.Builder> consumer,UploadFile... file) throws ImageException {
        Response response = null;
        try {
            MultipartBody.Builder body = new MultipartBody.Builder().setType(MultipartBody.FORM).addFormDataPart("folder",folder);
            if(!Strings.isNullOrEmpty(base64)){
                body.addFormDataPart("base64",base64);
            }
            if(file != null){
                for(UploadFile f : file){
                    checkArgument(f.getFile() != null || f.getContent() != null ,"必须传入图片内容");
                    String suffix = f.getExtension();
                    checkArgument(CONTENT_TYPE.containsKey(suffix.toLowerCase()),"不支持"+suffix+"格式图片上传");
                    var contentType = MediaType.parse(CONTENT_TYPE.getOrDefault(suffix, "io/github/buzzxu/thirdparty/image/jpeg"));
                    body.addFormDataPart("file",f.getFileName(),
                            f.getContent() != null ? RequestBody.create(f.getContent(),contentType) : createBinaryBody(contentType,f.getFile()));
                }
            }
            if(consumer != null){
                consumer.accept(body);
            }
            RequestBody requestBody = body.build();
            Request request = new Request.Builder()
                    .url(url+"/upload")
                    .post(requestBody)
                    .addHeader("Authorization",token)
                    .build();
            response = OkHttps.of().newCall(request).execute();

            if (!response.isSuccessful()) {
                JsonNode jsonNode;
                switch (response.code()){
                    case 401:
                    case 403:
                        throw new ForbiddenException("图片服务器认证失败，拒绝响应");
                    case 404:
                        throw new ImageException("上传图片地址有误,请检查",404);
                    case 413:
                        jsonNode = Jackson.of().readTree(response.body().string());
                        throw new ImageException("图片大小超过限制"+jsonNode.get("message").asText()+",请重新选择图片",413);
                    case 406:
                        throw new ImageException("图片格式有误,请更换后重新尝试",406);
                    default:{
                        jsonNode = Jackson.of().readTree(response.body().string());
                        throw new ImageException(jsonNode.findValue("message").asText(),response.code());
                    }
                }
            }
            JsonNode jsonNode = Jackson.of().readTree(response.body().string());
            if(!jsonNode.findValue("success").asBoolean()){
                throw new ImageException(jsonNode.findValue("message").asText(),500);
            }
            JsonNode fileNode = jsonNode.findValue("file");
            if(fileNode.isArray() && file != null){
                List<String> files = Lists.newArrayListWithCapacity(file.length);
                for (JsonNode node : fileNode) {
                    files.add(node.asText());
                }
                return files;
            }else{
                return Collections.singletonList(jsonNode.findValue("file").asText());
            }

        }catch (ForbiddenException ex){
            throw ex;
        }catch (ImageException ex){
            throw ex;
        }catch (ApplicationException ex){
            throw ex;
        }catch (Exception ex){
            throw ApplicationException.raise(ex);
        }finally {
           if(response!= null){
               response.close();
           }
        }
    }


    public boolean delete(String file,Consumer<HttpUrl.Builder> consumer) throws ImageException {
        return delete(consumer,file);
    }

    public boolean delete(Consumer<HttpUrl.Builder> consumer,String... files) throws ImageException {
        Response response = null;
        try {
            HttpUrl.Builder urlBuilder = HttpUrl.parse(url+ "/delete")
                    .newBuilder();
            for(String file : files){
                urlBuilder.addQueryParameter("file",file);
            }
            consumer.accept(urlBuilder);
            Request request = new Request.Builder()
                    .delete()
                    .addHeader("Authorization",token)
                    .url(urlBuilder.build())
                    .build();
            response = OkHttps.of().newCall(request).execute();
            if (!response.isSuccessful()) {
                if(response.code()>=400 && response.code()< 500){
                    if(response.code() == 401 || response.code() == 403){
                        throw new ForbiddenException("图片服务器认证失败，拒绝响应");
                    }
                    if(response.code() == 404){
                        return false;
                    }
                }else if(response.code() >= 500){
                    JsonNode jsonNode = Jackson.of().readTree(response.body().string());
                    throw new ImageException(jsonNode.findValue("message").asText(),response.code());
                }
                return false;
            }
            return  true;
        }catch (ForbiddenException ex){
            throw ex;
        }catch (ImageException ex){
            throw ex;
        }catch (ApplicationException ex){
            throw ex;
        } catch (Exception ex) {
            throw ApplicationException.raise(ex);
        } finally {
            if (response != null) {
                response.close();
            }
        }
    }

    public byte[] bytes(String path) throws IOException {
        Request request = new Request.Builder()
                .url(url + path)
                .build();
        try (Response response = OkHttps.of().newCall(request).execute()) {
            return response.body().bytes();
        }
    }


}

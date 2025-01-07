package io.github.buzzxu.thirdparty.image;


import io.github.buzzxu.spuddy.exceptions.ApplicationException;
import io.github.buzzxu.spuddy.util.UploadFile;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;

/**
 * @author xux
 * @date 2023年05月12日 9:43:27
 */
@Slf4j
public class BuzzUploadImager implements UploadImager{

    private final BuzzImager buzzImager;

    public BuzzUploadImager(BuzzImager buzzImager) {
        this.buzzImager = buzzImager;
    }

    public String upload(String folder, MultipartFile file){
        try {
            return upload(folder, UploadFile.of(file.getOriginalFilename(),file.getBytes()),true);
        } catch (IOException e) {
            log.error("上传失败，原因:{}",e.getMessage(),e);
            throw ApplicationException.notifyUser("上传图片失败,请稍后再试或联系管理员");
        }
    }

    public String upload(String folder,String fileName, byte[] data){
        return upload(folder,UploadFile.of(fileName,data),true);
    }

    @Override
    public String upload(String folder, String fileName, InputStream inputStream) {
        try {
            return buzzImager.upload(folder,fileName,inputStream,builder -> {});
        } catch (ImageException e) {
            log.error("上传失败，原因:{}",e.getMessage(),e);
            throw new IllegalArgumentException("上传失败,请稍后尝试");
        }
    }

    public String upload(String folder, UploadFile uploadFile,boolean webp){
        try {

            return buzzImager.upload(folder,uploadFile,builder -> {
                if(webp){
                    builder.addFormDataPart("webp","add");
                }
            });
        }catch (ImageException ex){
            throw ApplicationException.notifyUser(ex.getMessage());
        }catch (ApplicationException ex){
            throw ex;
        }catch (Exception ex){
            log.error("上传失败，原因:{}",ex.getMessage(),ex);
            throw ApplicationException.notifyUser("上传图片失败,请稍后再试或联系管理员");
        }
    }

    public boolean delete(String file){

        try {
            return buzzImager.delete(file,builder -> builder.addQueryParameter("webp",null));
        } catch (ImageException e) {
            throw new ApplicationException(e);
        }
    }
}

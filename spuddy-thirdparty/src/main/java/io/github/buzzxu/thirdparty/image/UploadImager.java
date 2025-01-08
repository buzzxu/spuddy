package io.github.buzzxu.thirdparty.image;


import io.github.buzzxu.spuddy.util.UploadFile;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;

/**
 * @author xux
 * @date 2023年05月12日 9:39:32
 */
public interface UploadImager {

    String upload(String folder, MultipartFile file);
    String upload(String folder, MultipartFile file,boolean rename,boolean webp);
    String upload(String folder,String fileName, byte[] data);
    default String upload(String folder,String fileName, byte[] data,boolean rename){
        return upload(folder,UploadFile.of(fileName,data,rename),true);
    }
    String upload(String folder,String fileName, byte[] data,boolean rename,boolean webp);
    String upload(String folder,String fileName, InputStream inputStream);
    String upload(String folder, UploadFile uploadFile, boolean webp);

    boolean delete(String file);
}

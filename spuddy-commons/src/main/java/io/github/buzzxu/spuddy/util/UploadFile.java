package io.github.buzzxu.spuddy.util;

import com.google.common.base.Strings;
import lombok.Getter;

import java.io.InputStream;
import java.util.UUID;

import static com.google.common.base.Preconditions.checkArgument;

/**
 * @program: 
 * @description:
 * @author: 徐翔
 * @create: 2018-06-30 22:56
 **/
@Getter
public class UploadFile {
    private String fileName;
    private InputStream file;
    private byte[] content;
    private String extension;
    private boolean rename;

    private UploadFile(String fileName, InputStream file) {
        this(fileName,file,true);
    }
    private UploadFile(String fileName, InputStream file,boolean rename) {
        checkArgument(!Strings.isNullOrEmpty(fileName),"Null filename used");
        this.fileName = fileName;
        this.file = file;
        fileExtension();
        this.rename = rename;
        if(rename){
            rename();
        }
    }

    public UploadFile(String fileName, byte[] content){
        this(fileName,content,true);
    }
    public UploadFile(String fileName, byte[] content,boolean rename) {
        checkArgument(!Strings.isNullOrEmpty(fileName),"Null filename used");
        this.fileName = fileName;
        this.content = content;
        this.rename = rename;
        fileExtension();
        if(rename){
            rename();
        }

    }

    public static UploadFile of(String fileName, InputStream file){
        return new UploadFile(fileName, file);
    }
    public static UploadFile of(String fileName, InputStream file,boolean rename){
        return new UploadFile(fileName, file,rename);
    }
    public static UploadFile of(String fileName, byte[] content){
        return new UploadFile(fileName, content);
    }
    public static UploadFile of(String fileName, byte[] content,boolean rename){
        return new UploadFile(fileName, content,rename);
    }

    public void rename(){
        fileName = UUID.randomUUID().toString().replaceAll("-","") + "."+extension;
    }

    private void fileExtension(){
        extension =  fileName.substring(fileName.lastIndexOf(".")+ 1);
    }
}

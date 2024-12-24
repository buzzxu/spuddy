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
    private UploadFile(String fileName, InputStream file) {
        checkArgument(!Strings.isNullOrEmpty(fileName),"Null filename used");
        this.fileName = fileName;
        this.file = file;
        fileExtension();
        rename();
    }

    public UploadFile(String fileName, byte[] content) {
        checkArgument(!Strings.isNullOrEmpty(fileName),"Null filename used");
        this.fileName = fileName;
        this.content = content;
        fileExtension();
        rename();
    }

    public static UploadFile of(String fileName, InputStream file){
        return new UploadFile(fileName, file);
    }
    public static UploadFile of(String fileName, byte[] content){
        return new UploadFile(fileName, content);
    }

    public void rename(){
        fileName = UUID.randomUUID().toString().replaceAll("-","") + "."+extension;
    }

    private void fileExtension(){
        extension =  fileName.substring(fileName.lastIndexOf(".")+ 1);
    }
}

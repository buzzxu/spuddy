package io.github.buzzxu.spuddy.util;

import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import com.google.common.io.Resources;
import io.github.buzzxu.spuddy.exceptions.ApplicationException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.FileVisitResult;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.List;
import java.util.function.Consumer;

import static com.google.common.base.Preconditions.checkArgument;

public class Files {



    public static String content(String path){
        checkArgument(!Strings.isNullOrEmpty(path),"param `path` not be null");
        try {
            return content(Resources.getResource(path.trim()).openStream());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    public static String content(URI uri){
        checkArgument(uri != null,"uri not be null");
        return content(Path.of(uri));
    }
    public static String content(URL url){
        checkArgument(url != null,"url not be null");
        try {
            return content(url.toURI());
        } catch (URISyntaxException e) {
            throw ApplicationException.raise(e);
        }
    }
    public static String content(Path path){
        checkArgument(path != null,"path not be null");
        try {
            return content(java.nio.file.Files.newInputStream(path));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    public static String content(InputStream stream){
        checkArgument(stream != null,"stream not be null");
        BufferedReader br = null;
        try {
            br = new BufferedReader(new InputStreamReader(stream));
            StringBuilder sb = new StringBuilder();
            String str ;
            while ((str = br.readLine()) != null) {
                sb.append(str).append(System.lineSeparator());
            }
            return sb.toString();
        }catch (IOException e) {
            throw new RuntimeException(e);
        }finally {
            if(stream != null){
                try {
                    stream.close();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        }
    }


    public static List<String> readLines(String path){
        BufferedReader br = null;
        try (InputStream stream = Resources.getResource(path).openStream()){
            br = new BufferedReader(new InputStreamReader(stream));
            List<String> list = Lists.newArrayList();
            String strLine;
            while ((strLine = br.readLine()) != null)   {
                list.add(strLine);
            }
            return list;
        }catch (IOException e) {
            throw new RuntimeException(e);
        }finally {
            if (br != null) {
                try {
                    br.close();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        }
    }

    /**
     * 遍历类路径下的文件
     * @param classpath
     * @param consumer
     * @throws URISyntaxException
     * @throws IOException
     */
    public static void walk(String classpath, Consumer<Path> consumer) throws URISyntaxException, IOException {
        walk(Paths.get(ClassLoader.getSystemResource(classpath).toURI()),consumer);
    }

    /**
     * 遍历根目录下的文件
     * @param start
     * @param consumer
     * @throws IOException
     */
    public static void walk(Path start, Consumer<Path> consumer) throws IOException {
        java.nio.file.Files.walkFileTree(start,new SimpleFileVisitor<>(){
            @Override
            public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                consumer.accept(file);
                return super.visitFile(file, attrs);
            }
        });
    }

    public static String fileExtension(String file){
        return com.google.common.io.Files.getFileExtension(file);
    }

    public static String nameWithoutExtension(String file){
        return com.google.common.io.Files.getNameWithoutExtension(file);
    }

    /**
     * 文件检查大小
     * @param len   文件长度
     * @param limit 限制大小
     * @param unit  限制单位 B,K,M,G
     * @return
     */
    public static boolean checkSize(long len,int limit,String unit){
        return switch (unit.toUpperCase()){
            case "B" -> (double)len;
            case "K" -> (double) len / 1024;
            case "M" -> (double) len / 1048576;
            case "G" -> (double) len / 1073741824;
            default -> throw new IllegalStateException("限制单位识别错误，请传入`B,K,M,G`");
        } < limit;
    }
}

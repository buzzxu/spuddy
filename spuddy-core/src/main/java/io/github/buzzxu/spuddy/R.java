package io.github.buzzxu.spuddy;

import lombok.Getter;

import java.util.function.Consumer;

/**
 * @author 徐翔
 * @create 2021-08-25 15:08
 **/
public class R<T> {
    @Getter
    private final int code;
    @Getter
    private final boolean error;
    @Getter
    private String message;
    @Getter
    private T data;

    private R(){
        this(0);
    }
    private R(int code){
        this(code,null);
    }
    private R(int code, String message){
        this.code = code;
        this.message = message;
        this.error = this.code > 0;
    }
    @SuppressWarnings("unchecked")
    public static <T> R<T> of(T data){
        return new R<>().data(data);
    }
    public static <T> R<T> of(){
        return new R<>();
    }
    @SuppressWarnings("unchecked")
    public static <T> R<T> of(int code, T data){
        return new R<>(code).data(data);
    }
    public static <T> R<T> code(int code){
        return new R<>(code);
    }

    @SuppressWarnings("rawtypes")
    public void to(Consumer<R> action){
        action.accept(this);
    }
    public static <T> R<T> error(int code, String message){
        return new R<>(code,message);
    }
    public static <T> R<T> error(int code){
        return new R<>(code,null);
    }


    @SuppressWarnings("rawtypes")
    public R message(String message){
        this.message = message;
        return this;
    }
    @SuppressWarnings("rawtypes")
    public R data(T data){
        this.data = data;
        return this;
    }
}

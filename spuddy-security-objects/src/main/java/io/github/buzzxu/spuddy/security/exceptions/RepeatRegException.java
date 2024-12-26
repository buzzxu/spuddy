package io.github.buzzxu.spuddy.security.exceptions;

/**
 * @author 徐翔
 * @since 2021-11-15 14:58
 **/
public class RepeatRegException extends IllegalArgumentException{

    private String tag;

    public RepeatRegException(String message, String tag) {
        super(message);
        this.tag = tag;
    }
    public RepeatRegException(String message) {
        this(message,null);
    }



    public RepeatRegException tag(String tag){
        this.tag = tag;
        return this;
    }

    public String tag(){
        return tag;
    }

}

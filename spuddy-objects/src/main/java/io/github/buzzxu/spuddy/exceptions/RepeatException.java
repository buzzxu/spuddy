package io.github.buzzxu.spuddy.exceptions;

/**
 * @author 徐翔
 * @since 2021-11-15 15:29
 **/
public class RepeatException extends IllegalArgumentException{
    private final String tag;

    public RepeatException(String message) {
        this(message,null);
    }

    public RepeatException(String message, String tag) {
        super(message);
        this.tag = tag;
    }

    public String tag() {
        return tag;
    }
}

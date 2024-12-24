package io.github.buzzxu.spuddy.errors;

/**
 * Created by xux on 2017/4/23.
 */
public interface ErrorCode {
    String code();

    int status();

    String message();
}

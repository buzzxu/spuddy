package io.github.buzzxu.thirdparty.image;

public class ImageException extends Exception {
    private final int code;

    public ImageException(String message, int code) {
        super(message);
        this.code = code;
    }

    public ImageException(String message, Throwable cause, int code) {
        super(message, cause);
        this.code = code;
    }

    public ImageException(Throwable cause, int code) {
        super(cause);
        this.code = code;
    }
}

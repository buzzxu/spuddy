package io.github.buzzxu.spuddy.security;

/**
 * @author 徐翔
 * @create 2021-08-26 11:01
 **/
public class GetMeToken {

    private static final ThreadLocal<String> thread = new ThreadLocal<>();

    public static void set(String token) {
        thread.set(token);
    }

    public static String of(){
        return thread.get();
    }

    public static void clear() {
        thread.remove();
    }
}

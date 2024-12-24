package io.github.buzzxu.spuddy.security.annotation;

import java.lang.annotation.*;

/**
 * @author 徐翔
 * @create 2021-08-26 10:13
 **/
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE,ElementType.METHOD})
@Documented
public @interface RequiresUser {
}

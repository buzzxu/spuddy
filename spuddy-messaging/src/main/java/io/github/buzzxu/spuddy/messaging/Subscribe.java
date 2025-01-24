package io.github.buzzxu.spuddy.messaging;

import org.springframework.context.annotation.Import;
import org.springframework.stereotype.Component;

import java.lang.annotation.*;

/**
 * @author xux
 * @date 2025年01月18日 15:32:51
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Component
public @interface Subscribe {
    String name() default "";

    String group() default "";

    String topic() default "";

    String tag() default "";
    /**
     * 是否重复消费
     *
     * @return
     */
    boolean repeat() default false;

    int count() default 1;

    int expired() default 1;
}

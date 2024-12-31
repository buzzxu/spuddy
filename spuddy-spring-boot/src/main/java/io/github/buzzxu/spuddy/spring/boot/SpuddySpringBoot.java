package io.github.buzzxu.spuddy.spring.boot;


import io.github.buzzxu.spuddy.Stage;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.data.web.SpringDataWebAutoConfiguration;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.stereotype.Component;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author 徐翔
 * @create 2021-08-25 15:42
 **/
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@SpringBootApplication(exclude = SpringDataWebAutoConfiguration.class,scanBasePackages = {})
public @interface SpuddySpringBoot {
    Stage stage() default Stage.DEFAULT;
    String name() default "";
    String[] packages() default {};
}

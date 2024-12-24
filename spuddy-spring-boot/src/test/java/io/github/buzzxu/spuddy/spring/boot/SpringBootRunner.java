package io.github.buzzxu.spuddy.spring.boot;

import org.junit.jupiter.api.Test;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * @author 徐翔
 * @create 2021-08-25 15:11
 **/
@SpuddySpringBoot
public class SpringBootRunner {

    @Test
    public void start(){
        main(new String[]{});
    }

    public static void main(String[] args) {
        new SpringBootApp( ).run(SpringBootRunner.class,args);
    }
}

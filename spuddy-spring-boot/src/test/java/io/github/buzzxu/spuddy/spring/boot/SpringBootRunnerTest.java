package io.github.buzzxu.spuddy.spring.boot;

import org.junit.jupiter.api.Test;

/**
 * @author 徐翔
 * @create 2021-08-25 15:11
 **/

@SpuddySpringBoot
public class SpringBootRunnerTest {

    @Test
    public void start(){
        main(new String[]{});
    }

    public static void main(String[] args) {
        try {
            new SpringBootApp( ).run(SpringBootRunnerTest.class,args);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}

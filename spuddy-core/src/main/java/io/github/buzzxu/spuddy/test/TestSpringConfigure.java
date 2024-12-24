package io.github.buzzxu.spuddy.test;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;

/**
 * @program: 
 * @description:
 * @author: xux
 * @create: 2021-09-07 19:28
 **/
@Configuration
public class TestSpringConfigure {
    @Bean
    public PropertySourcesPlaceholderConfigurer propertiesResolver(){
        return new PropertySourcesPlaceholderConfigurer();
    }
}

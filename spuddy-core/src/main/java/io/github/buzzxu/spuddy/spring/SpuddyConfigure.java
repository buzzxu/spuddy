package io.github.buzzxu.spuddy.spring;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.buzzxu.spuddy.jackson.Jackson;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

/**
 * @author 徐翔
 * @create 2021-08-25 15:40
 **/
@Configuration
public class SpuddyConfigure {

    @Bean
    @Primary
    public ObjectMapper objectMapper(){
        return Jackson.INSTANCE.objectMapper();
    }
}

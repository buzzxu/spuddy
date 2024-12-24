package io.github.buzzxu.spuddy.spring.boot;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;

/**
 * @author 徐翔
 * @create 2021-08-25 16:01
 **/
public class SpringBootApp {
    private Logger logger;

    public SpringBootApp() {
        this.logger = LoggerFactory.getLogger("SPUDDY");
    }

    public SpringBootApp(Logger logger) {
        this.logger = logger;
    }

    public void run(Class<?> mainClass,String... args){
        SpringApplication app=new SpringApplication(mainClass);
        app.addInitializers(new SpuddyContextInitializer(mainClass,logger));
        app.run(args);
        return ;
    }
}

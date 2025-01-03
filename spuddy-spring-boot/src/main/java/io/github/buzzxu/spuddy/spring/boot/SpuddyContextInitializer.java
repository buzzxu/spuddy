package io.github.buzzxu.spuddy.spring.boot;


import io.github.buzzxu.spuddy.Env;
import io.github.buzzxu.spuddy.Stage;
import io.github.buzzxu.spuddy.errors.NotFoundException;
import io.github.buzzxu.spuddy.spring.AbstractSpringInitializer;
import org.slf4j.Logger;
import org.springframework.boot.web.servlet.context.AnnotationConfigServletWebServerApplicationContext;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.context.ConfigurableWebApplicationContext;

/**
 * @author 徐翔
 * @create 2021-08-25 15:43
 **/
public class SpuddyContextInitializer extends AbstractSpringInitializer<ConfigurableApplicationContext >  {
    private Class<?> mainClass;

    public SpuddyContextInitializer(Class<?> mainClass, Logger logger) {
        super(logger);
        this.mainClass = mainClass;
    }

    @Override
    public void initialize(ConfigurableApplicationContext  applicationContext) {
        SpuddySpringBoot spuddy ;
        Stage stage = null;
        if(mainClass.isAnnotationPresent(SpuddySpringBoot.class)){
            spuddy = mainClass.getAnnotation(SpuddySpringBoot.class);
        }else{
            throw new NotFoundException(mainClass + " not found @SpuddySpringBoot");
        }
        if(applicationContext.getEnvironment().getActiveProfiles().length >0){
            stage = Stage.N(applicationContext.getEnvironment().getActiveProfiles()[0]);
        }
        if (stage == null ) {
            stage = spuddy.stage();
        }
        String[] packages = spuddy.packages();
        if (packages.length ==0) {
            packages = new String[]{mainClass.getPackage().getName()};
        }
        initializer(applicationContext,new Env(mainClass,applicationContext,applicationContext.getApplicationName(),stage,packages,logger));
    }
}

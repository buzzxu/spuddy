package io.github.buzzxu.spuddy.test;


import io.github.buzzxu.spuddy.Env;
import io.github.buzzxu.spuddy.Stage;
import io.github.buzzxu.spuddy.spring.AbstractSpringInitializer;
import org.slf4j.LoggerFactory;
import org.springframework.context.ConfigurableApplicationContext;

/**
 * @description:
 * @author: xux
 * @create: 2021-09-07 19:21
 **/
public class TestSpringInitializer extends AbstractSpringInitializer<ConfigurableApplicationContext> {
    private Class<?> mainClass;
    public TestSpringInitializer(Class<?> mainClass) {
        super(LoggerFactory.getLogger("test"));
        this.mainClass = mainClass;
    }

    @Override
    public void initialize(ConfigurableApplicationContext applicationContext) {
        Stage stage;
        if(applicationContext.getEnvironment().getActiveProfiles().length >0){
            stage = Stage.N(applicationContext.getEnvironment().getActiveProfiles()[0]);
        }else{
            stage = Stage.DEFAULT;
        }
        initializer(applicationContext,new Env(mainClass,applicationContext,"test",stage,new String[]{mainClass.getPackageName()},logger));
    }
}

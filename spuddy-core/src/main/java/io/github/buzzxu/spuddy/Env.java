package io.github.buzzxu.spuddy;

import com.google.common.base.Joiner;
import lombok.Getter;
import lombok.Setter;
import org.slf4j.Logger;
import org.springframework.context.ApplicationContext;

/**
 * @author 徐翔
 * @create 2021-08-25 15:35
 **/
public class Env implements AutoCloseable{

    @Getter
    protected String name;
    @Getter
    protected Class<?> mainClass;
    @Getter @Setter
    protected boolean allowCircularReferences;
    protected Logger logger;
    @Getter
    protected String[] basePackages;
    @Getter
    protected Stage stage;
    @Getter
    protected ApplicationContext applicationContext;

    public Env(Class<?> mainClass, ApplicationContext applicationContext,String name, Stage stage,String[] basePackages, Logger logger){
        this.mainClass = mainClass;
        this.applicationContext = applicationContext;
        this.name = name;
        this.stage = stage;
        this.basePackages = basePackages;
        this.logger = logger;
        System.setProperty("packages", Joiner.on(",").join(basePackages));
    }
    @Override
    public void close() throws Exception {

    }

    public Logger logger(){
        return logger;
    }
}

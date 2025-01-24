package io.github.buzzxu.spuddy;

import com.google.common.base.Joiner;
import com.google.common.base.Strings;
import io.github.buzzxu.spuddy.exceptions.StartupException;
import io.github.buzzxu.spuddy.util.id.Id;
import lombok.Getter;
import lombok.Setter;
import org.slf4j.Logger;
import org.springframework.context.ApplicationContext;
import org.springframework.core.env.Environment;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * @author 徐翔
 * @create 2021-08-25 15:35
 **/
public class Env implements AutoCloseable{

    @Getter
    protected String appId;
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
        appId = appId();
        logger.info("Plugins [init] APPID: {}",appId);
        System.setProperty("packages", Joiner.on(",").join(basePackages));
    }
    @Override
    public void close() throws Exception {

    }

    public Logger logger(){
        return logger;
    }



    private String appId(){
        String homeDir = System.getProperty("user.home");
        Path path = Paths.get(homeDir, ".app", "appId");
        String appId;

        try {
            if (!Files.exists(path.getParent())) {
                Files.createDirectories(path.getParent());
            }

            if (Files.exists(path)) {
                appId = new String(Files.readAllBytes(path));
            } else {
                appId = Strings.nullToEmpty(this.name)+"-"+ Id.nextId();
                if(stage == Stage.DOCKER || stage == Stage.PRODUCT ){
                    Files.write(path, appId.getBytes());
                }
            }
            return appId;
        } catch (IOException e) {
            throw new StartupException("生成APPID 失败,原因: "+ e.getMessage());
        }
    }
}

package io.github.buzzxu.spuddy.util;

import lombok.Getter;
import org.reflections.Reflections;
import org.reflections.scanners.Scanners;
import org.reflections.util.ConfigurationBuilder;
import org.reflections.util.FilterBuilder;

import java.lang.annotation.Annotation;
import java.util.Set;
import java.util.regex.Pattern;

/**
 * @author xux
 */
public class Reflects {


    @Getter
    protected Reflections reflections;



    public Reflects(String... packages){
        FilterBuilder filterBuilder = new FilterBuilder();
        for (String pkg :packages){
            filterBuilder.includePackage(pkg.trim());
        }
        reflections = new Reflections(new ConfigurationBuilder()
                .forPackages(packages)
                .filterInputsBy(filterBuilder)
                .setScanners(
                        Scanners.SubTypes,
                        Scanners.TypesAnnotated
                ));
    }

    /**
     * 获取应用包名下某父类(或接口)的所有子类(或实现类)
     * @param superClass
     * @return
     */
    public <T> Set<Class<? extends T>> getSubTypesOf(final Class<T> superClass)  {
        return reflections.getSubTypesOf(superClass);
    }

    /**
     * 获取应用包名下带有某注解的所有类
     *
     * @param annotationClass
     * @return
     */
    public  Set<Class<?>> getTypesAnnotatedWith(Class<? extends Annotation> annotationClass) {
        return reflections.getTypesAnnotatedWith(annotationClass);
    }

    public Set<String> getResources(String regex){
        return reflections.getResources(Pattern.compile(regex));
    }
}

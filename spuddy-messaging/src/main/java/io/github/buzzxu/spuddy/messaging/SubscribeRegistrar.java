package io.github.buzzxu.spuddy.messaging;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.GenericBeanDefinition;
import org.springframework.context.annotation.ClassPathScanningCandidateComponentProvider;
import org.springframework.context.annotation.ImportBeanDefinitionRegistrar;
import org.springframework.core.type.AnnotationMetadata;
import org.springframework.core.type.filter.AnnotationTypeFilter;

/**
 * @author xux
 * @date 2025年01月18日 19:31:59
 */
@Slf4j
public class SubscribeRegistrar implements ImportBeanDefinitionRegistrar {
    @Override
    public void registerBeanDefinitions(AnnotationMetadata importingClassMetadata, BeanDefinitionRegistry registry) {
        // 创建扫描器
        ClassPathScanningCandidateComponentProvider scanner = new ClassPathScanningCandidateComponentProvider(false);
        // 添加CustomService注解过滤器
        scanner.addIncludeFilter(new AnnotationTypeFilter(Subscribe.class));
        String basePackage = importingClassMetadata.getClassName();
        basePackage = basePackage.substring(0, basePackage.lastIndexOf('.'));

        // 扫描包下所有带有CustomService注解的类
        for (BeanDefinition beanDefinition : scanner.findCandidateComponents(basePackage)) {
            String beanClassName = beanDefinition.getBeanClassName();
            try {
                Class<?> beanClass = Class.forName(beanClassName);
                BeanDefinitionBuilder builder = BeanDefinitionBuilder.genericBeanDefinition(beanClass);
                String beanName = Character.toLowerCase(beanClass.getSimpleName().charAt(0)) +
                        beanClass.getSimpleName().substring(1);
                registry.registerBeanDefinition(beanName, builder.getBeanDefinition());

            } catch (ClassNotFoundException e) {
               log.error("{}",e.getMessage(),e);
            }
        }
    }
}

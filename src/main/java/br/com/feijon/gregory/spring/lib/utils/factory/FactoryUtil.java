package br.com.feijon.gregory.spring.lib.utils.factory;

import lombok.NoArgsConstructor;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;

@Service
@NoArgsConstructor
public class FactoryUtil implements ApplicationContextAware {
    private static ApplicationContext context;
    private static final Object LOCK = new Object();

    public static <T> T getBean(Class<T> beanClass) {
        return context.getBean(beanClass);
    }

    public static <T> T getBeanFromName(String beanName, Class<T> beanClass) {
        return context.getBean(beanName, beanClass);
    }

    @Override
    public void setApplicationContext(@NonNull ApplicationContext applicationContext) throws BeansException {
        synchronized (LOCK) {
            if (context == null) {
                context = applicationContext;
            }
        }
    }
}

package org.phoenicis.scripts.engine.injectors;

import org.graalvm.polyglot.Value;
import org.phoenicis.configuration.security.Safe;
import org.phoenicis.scripts.engine.implementation.PhoenicisScriptEngine;
import org.springframework.context.ApplicationContext;

import java.lang.annotation.Annotation;
import java.util.function.Function;

/**
 * Injects Bean() function into a Script Engine
 */
public class BeanInjector implements EngineInjector<Value> {
    private final ApplicationContext applicationContext;

    public BeanInjector(ApplicationContext applicationContext) {
        this.applicationContext = applicationContext;
    }

    @Override
    public void injectInto(PhoenicisScriptEngine phoenicisScriptEngine) {
        phoenicisScriptEngine.put("Bean", (Function<String, Object>) this::fetchBean);
    }

    private Object fetchBean(String beanName) {
        final Object bean = applicationContext.getBean(beanName);
        final Class<?> beanClass = bean.getClass();

        for (Annotation annotation : beanClass.getAnnotations()) {
            if (annotation.annotationType() == Safe.class) {
                return bean;
            }
        }

        throw new IllegalAccessError(String.format("You are not allowed to instantiate %s (of class %s) from a script.",
                beanName, beanClass.getName()));
    }
}

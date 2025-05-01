package io.github.gregoryfeijon.domain.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface EnumUseAttributeInMarshalling {

    String serializeAttributeName() default "";
    String deserializeAttributeName() default "";
    String defaultAttributeName() default "";
}

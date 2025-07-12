package io.github.gregoryfeijon.domain.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotation that provides configuration for object construction during deep copying.
 * <p>
 * This annotation is used by {@link io.github.gregoryfeijon.utils.serialization.ObjectFactoryUtil}
 * to control which fields should be excluded when creating a copy of an object.
 * <p>
 * Example usage:
 * <pre>
 * &#64;ObjectConstructor(exclude = {"password", "temporaryData"})
 * public class User {
 *     private String username;
 *     private String password;
 *     private Map<String, Object> temporaryData;
 * }
 * </pre>
 *
 * @author gregory.feijon
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE})
public @interface ObjectConstructor {

    /**
     * Specifies field names that should be excluded during object copying.
     * <p>
     * Fields listed here will not be copied from the source object to the
     * destination object when using {@link io.github.gregoryfeijon.utils.serialization.ObjectFactoryUtil}.
     *
     * @return Array of field names to exclude
     */
    String[] exclude() default {};
}
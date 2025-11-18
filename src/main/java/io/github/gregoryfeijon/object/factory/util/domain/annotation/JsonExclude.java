package io.github.gregoryfeijon.object.factory.util.domain.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotation that marks a field to be excluded from serialization and deserialization.
 * <p>
 * When applied to a field, this annotation instructs the serialization framework
 * (Gson or Jackson) to ignore the field during both serialization and deserialization
 * processes.
 * <p>
 * Example usage:
 * <pre>
 * public class User {
 *     private String username;
 *
 *     &#64;Exclude
 *     private String password;
 * }
 * </pre>
 *
 * @author gregory.feijon
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface JsonExclude {
}

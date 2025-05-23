package io.github.gregoryfeijon.domain.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotation that specifies which attribute of an enum should be used during serialization and deserialization.
 * <p>
 * This annotation allows customizing how enum values are represented in JSON by using
 * a field value instead of the enum name. It provides separate control for serialization
 * and deserialization processes, with a fallback to a default attribute.
 * <p>
 * Example usage:
 * <pre>
 * public enum PaymentType {
 *     @EnumUseAttributeInMarshalling(defaultAttributeName = "code")
 *     CREDIT_CARD("CC"),
 *     @EnumUseAttributeInMarshalling(defaultAttributeName = "code")
 *     DEBIT_CARD("DC");
 *
 *     private final String code;
 *
 *     PaymentType(String code) {
 *         this.code = code;
 *     }
 * }
 * </pre>
 *
 * @author gregory.feijon
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface EnumUseAttributeInMarshalling {

    /**
     * Specifies the name of the attribute to use during serialization.
     * <p>
     * This attribute takes precedence over deserializeAttributeName and defaultAttributeName
     * when serializing an enum value.
     *
     * @return The name of the attribute to use during serialization
     */
    String serializeAttributeName() default "";

    /**
     * Specifies the name of the attribute to use during deserialization.
     * <p>
     * This attribute takes precedence over defaultAttributeName when deserializing
     * to an enum value, but is used only if serializeAttributeName is not specified.
     *
     * @return The name of the attribute to use during deserialization
     */
    String deserializeAttributeName() default "";

    /**
     * Specifies the default attribute name to use when neither serializeAttributeName
     * nor deserializeAttributeName is specified.
     *
     * @return The default attribute name to use
     */
    String defaultAttributeName() default "";
}

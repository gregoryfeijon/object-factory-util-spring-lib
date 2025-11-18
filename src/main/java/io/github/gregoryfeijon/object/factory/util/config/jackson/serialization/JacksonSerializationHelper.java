package io.github.gregoryfeijon.object.factory.util.config.jackson.serialization;

import io.github.gregoryfeijon.object.factory.util.domain.annotation.EnumUseAttributeInMarshalling;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.ReflectionUtils;
import org.springframework.util.StringUtils;

import java.lang.reflect.Field;
import java.util.Objects;

/**
 * Helper class for Jackson serialization and deserialization operations.
 * <p>
 * This utility class provides common methods used by Jackson serializers and deserializers,
 * particularly for handling enum types with custom attribute-based serialization.
 *
 * @author gregory.feijon
 */
@Slf4j
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class JacksonSerializationHelper {

    /**
     * Retrieves the {@link EnumUseAttributeInMarshalling} annotation from an enum constant.
     *
     * @param value    The enum constant
     * @param enumType The enum class
     * @return The annotation, or null if not present
     * @throws IllegalStateException If enumType is null
     */
    public static EnumUseAttributeInMarshalling getEnumUseAttributeInMarshallingAnnotation(Enum<?> value, Class<? extends Enum<?>> enumType) {
        if (enumType == null) {
            throw new IllegalStateException("enumType must not be null after contextualization");
        }
        try {
            Field field = enumType.getField(value.name());
            return field.getAnnotation(EnumUseAttributeInMarshalling.class);
        } catch (NoSuchFieldException e) {
            log.warn("Field with the specified name not found: {}", value.name(), e);
        }
        return null;
    }

    /**
     * Determines if an enum constant matches the given attribute value.
     *
     * @param enumValue      The enum constant to check
     * @param attributeName  The attribute name to use for comparison
     * @param attributeValue The attribute value to match against
     * @param enumType       The enum class
     * @return true if the enum matches the attribute value, false otherwise
     */
    public static boolean isValidEnum(Enum<?> enumValue, String attributeName, String attributeValue, Class<? extends Enum<?>> enumType) {
        if (!StringUtils.hasText(attributeName)) {
            return enumValue.name().equals(attributeValue);
        } else {
            String attributeValueFromEnum = getAttributeValue(enumValue, attributeName, enumType);
            return Objects.equals(attributeValueFromEnum, attributeValue);
        }
    }

    /**
     * Retrieves the value of a specified attribute from an enum constant.
     *
     * @param value         The enum constant
     * @param attributeName The name of the attribute to retrieve
     * @param enumType      The enum class
     * @return The attribute value, or null if not found
     * @throws IllegalStateException If enumType is null
     */
    public static String getAttributeValue(Enum<?> value, String attributeName, Class<? extends Enum<?>> enumType) {
        if (value != null && attributeName != null) {
            if (enumType == null) {
                throw new IllegalStateException("enumType must not be null after contextualization");
            }
            Field field = ReflectionUtils.findField(enumType, attributeName);
            if (field != null) {
                ReflectionUtils.makeAccessible(field);
                return (String) ReflectionUtils.getField(field, value);
            }
        }
        return null;
    }
}
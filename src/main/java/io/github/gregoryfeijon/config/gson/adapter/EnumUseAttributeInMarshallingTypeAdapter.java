package io.github.gregoryfeijon.config.gson.adapter;

import com.google.gson.TypeAdapter;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;
import com.google.gson.stream.JsonWriter;
import io.github.gregoryfeijon.domain.annotation.EnumUseAttributeInMarshalling;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.ReflectionUtils;
import org.springframework.util.StringUtils;

import java.io.IOException;
import java.lang.reflect.Field;
import java.util.Objects;
import java.util.Optional;

/**
 * Type adapter for Gson that allows serialization and deserialization of enum values
 * based on an attribute value instead of the enum name.
 * <p>
 * This adapter uses the {@link EnumUseAttributeInMarshalling} annotation to determine
 * which attribute value should be used during serialization/deserialization processes.
 *
 * @param <T> The enum type to adapt
 * @author gregory.feijon
 */
@Slf4j
@RequiredArgsConstructor
public class EnumUseAttributeInMarshallingTypeAdapter<T> extends TypeAdapter<T> {

    private final Class<? super T> enumClass;

    /**
     * Constructs a new adapter for the specified enum type.
     *
     * @param type The TypeToken representing the enum type
     */
    public EnumUseAttributeInMarshallingTypeAdapter(TypeToken<T> type) {
        this.enumClass = type.getRawType();
    }

    /**
     * Writes the JSON representation of an enum value.
     * <p>
     * If the enum value is annotated with {@link EnumUseAttributeInMarshalling},
     * the specified attribute's value will be used instead of the enum name.
     *
     * @param out The JSON writer
     * @param value The enum value to write
     * @throws IOException If an I/O error occurs
     */
    @Override
    public void write(JsonWriter out, T value) throws IOException {
        if (enumClass.isEnum()) {
            if (value == null) {
                out.nullValue();
                return;
            }
            Enum<?> enumValue = (Enum<?>) value;
            EnumUseAttributeInMarshalling useAttribute = getEnumUseAttributeInMarshallingAnnotation(enumValue);
            String attributeName = Optional.ofNullable(useAttribute)
                    .map(EnumUseAttributeInMarshallingTypeAdapter::getAttributeName)
                    .orElse(null);

            if (attributeName != null) {
                String attributeValue = getAttributeValue(enumValue, attributeName);
                out.value(attributeValue);
            } else {
                out.value(enumValue.name());
            }
        }
    }

    /**
     * Reads a JSON value and converts it to an enum constant.
     * <p>
     * If the enum is annotated with {@link EnumUseAttributeInMarshalling},
     * the method will match the JSON value against the specified attribute's value.
     *
     * @param in The JSON reader
     * @return The enum constant, or null if no match is found
     * @throws IOException If an I/O error occurs
     */
    @Override
    public T read(JsonReader in) throws IOException {
        if (in.peek() == JsonToken.NULL) {
            in.nextNull();
            return null;
        }
        String attributeValue = in.nextString();
        if (attributeValue != null && enumClass.isEnum()) {
            for (Object value : enumClass.getEnumConstants()) {
                Enum<?> enumValue = (Enum<?>) value;
                EnumUseAttributeInMarshalling useAttribute = getEnumUseAttributeInMarshallingAnnotation(enumValue);
                String attributeName = Optional.ofNullable(useAttribute)
                        .map(EnumUseAttributeInMarshallingTypeAdapter::getAttributeName)
                        .orElse(null);

                if (isValidEnum(enumValue, attributeName, attributeValue)) {
                    return (T) enumValue;
                }
            }
        }
        return null;
    }

    /**
     * Retrieves the {@link EnumUseAttributeInMarshalling} annotation from an enum constant.
     *
     * @param value The enum constant
     * @return The annotation, or null if not present
     */
    private EnumUseAttributeInMarshalling getEnumUseAttributeInMarshallingAnnotation(Enum<?> value) {
        try {
            Field field = enumClass.getField(value.name());
            return field.getAnnotation(EnumUseAttributeInMarshalling.class);
        } catch (NoSuchFieldException e) {
            log.warn("Field with the specified name not found");
        }
        return null;
    }

    /**
     * Extracts the attribute name to use from the annotation.
     * <p>
     * Prioritizes in order: serializeAttributeName, deserializeAttributeName, defaultAttributeName.
     *
     * @param useAttribute The annotation
     * @return The attribute name, or null if none is specified
     */
    private static String getAttributeName(EnumUseAttributeInMarshalling useAttribute) {
        if (useAttribute != null) {
            String serializeAttributeName = useAttribute.serializeAttributeName();
            String deserializeAttributeName = useAttribute.deserializeAttributeName();
            String defaultAttributeName = useAttribute.defaultAttributeName();

            if (!serializeAttributeName.isEmpty()) {
                return serializeAttributeName;
            }

            if (!deserializeAttributeName.isEmpty()) {
                return deserializeAttributeName;
            }

            if (!defaultAttributeName.isEmpty()) {
                return defaultAttributeName;
            }
        }
        return null;
    }

    /**
     * Determines if an enum constant matches the given attribute value.
     *
     * @param enumValue The enum constant to check
     * @param attributeName The attribute name to use for comparison
     * @param attributeValue The attribute value to match against
     * @return true if the enum matches the attribute value, false otherwise
     */
    private boolean isValidEnum(Enum<?> enumValue, String attributeName, String attributeValue) {
        if (!StringUtils.hasText(attributeName)) {
            return enumValue.name().equals(attributeValue);
        } else {
            String attributeValueFromEnum = getAttributeValue(enumValue, attributeName);
            return Objects.equals(attributeValueFromEnum, attributeValue);
        }
    }

    /**
     * Retrieves the value of a specified attribute from an enum constant.
     *
     * @param value The enum constant
     * @param attributeName The name of the attribute to retrieve
     * @return The attribute value, or null if not found
     */
    private String getAttributeValue(Enum<?> value, String attributeName) {
        if (value != null && attributeName != null) {
            Field field = ReflectionUtils.findField(enumClass, attributeName);
            if (field != null) {
                ReflectionUtils.makeAccessible(field);
                return (String) ReflectionUtils.getField(field, value);
            }
        }
        return null;
    }
}
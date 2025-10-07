package io.github.gregoryfeijon.config.jackson.factory;

import com.fasterxml.jackson.databind.BeanDescription;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializationConfig;
import com.fasterxml.jackson.databind.ser.Serializers;
import io.github.gregoryfeijon.config.jackson.serialization.serializer.EnumUseAttributeSerializer;
import org.springframework.stereotype.Component;

/**
 * Custom Jackson serializers resolver for enum types.
 * <p>
 * This class registers a custom serializer for enum types to support
 * serialization based on enum attributes rather than enum names.
 *
 * @author gregory.feijon
 */
@Component
public class EnumSerializers extends Serializers.Base {

    /**
     * Finds a serializer for a given type.
     * <p>
     * For enum types, returns an {@link EnumUseAttributeSerializer}.
     * For other types, returns null to use Jackson's default serializers.
     *
     * @param config The serialization configuration
     * @param type The type to find a serializer for
     * @param beanDesc The bean description
     * @return A serializer for the type, or null if this resolver doesn't handle the type
     */
    @Override
    public JsonSerializer<?> findSerializer(SerializationConfig config, JavaType type, BeanDescription beanDesc) {
        if (type.isEnumType()) {
            return new EnumUseAttributeSerializer();
        }
        return null;
    }
}
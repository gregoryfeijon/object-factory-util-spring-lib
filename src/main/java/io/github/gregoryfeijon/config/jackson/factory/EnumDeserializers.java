package io.github.gregoryfeijon.config.jackson.factory;

import io.github.gregoryfeijon.config.jackson.serialization.deserializer.EnumUseAttributeDeserializer;
import com.fasterxml.jackson.databind.BeanDescription;
import com.fasterxml.jackson.databind.DeserializationConfig;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.deser.Deserializers;
import org.springframework.stereotype.Component;

/**
 * Custom Jackson deserializers resolver for enum types.
 * <p>
 * This class registers a custom deserializer for enum types to support
 * deserialization based on enum attributes rather than enum names.
 *
 * @author gregory.feijon
 */
@Component
public class EnumDeserializers extends Deserializers.Base {

    /**
     * Finds a deserializer for enum types.
     * <p>
     * For enum types, returns an {@link EnumUseAttributeDeserializer}.
     * For other types, returns null to use Jackson's default deserializers.
     *
     * @param type The type to find a deserializer for
     * @param config The deserialization configuration
     * @param beanDesc The bean description
     * @return A deserializer for the type, or null if this resolver doesn't handle the type
     */
    @Override
    public JsonDeserializer<?> findEnumDeserializer(Class<?> type,
                                                    DeserializationConfig config,
                                                    BeanDescription beanDesc) {
        if (type.isEnum()) {
            @SuppressWarnings("unchecked")
            Class<Enum<?>> enumClass = (Class<Enum<?>>) type;
            return new EnumUseAttributeDeserializer(enumClass);
        }
        return null;
    }
}
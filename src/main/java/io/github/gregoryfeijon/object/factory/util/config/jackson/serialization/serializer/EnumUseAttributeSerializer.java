package io.github.gregoryfeijon.object.factory.util.config.jackson.serialization.serializer;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.BeanProperty;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.ContextualSerializer;
import io.github.gregoryfeijon.object.factory.util.domain.annotation.EnumUseAttributeInMarshalling;
import io.github.gregoryfeijon.object.factory.util.utils.enums.EnumMarshallingUtil;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.util.Optional;

import static io.github.gregoryfeijon.object.factory.util.config.jackson.serialization.JacksonSerializationHelper.getAttributeValue;
import static io.github.gregoryfeijon.object.factory.util.config.jackson.serialization.JacksonSerializationHelper.getEnumUseAttributeInMarshallingAnnotation;
import static io.github.gregoryfeijon.object.factory.util.config.jackson.serialization.JacksonSerializationHelper.isValidEnum;

/**
 * Custom Jackson serializer for enum types that uses the {@link EnumUseAttributeInMarshalling} annotation.
 * <p>
 * This serializer allows enums to be serialized using a custom attribute value
 * instead of the default enum name.
 */
@Slf4j
public class EnumUseAttributeSerializer extends JsonSerializer<Enum<?>> implements ContextualSerializer {

    /**
     * The enum class this serializer handles.
     */
    private final Class<? extends Enum<?>> enumType;

    /**
     * Default constructor.
     * <p>
     * This constructor is needed for the serializer to be registered as a module.
     * The actual enum type will be determined during contextualization.
     */
    public EnumUseAttributeSerializer() {
        this.enumType = null;
    }

    /**
     * Constructor with a specific enum type.
     *
     * @param enumType The enum class to serialize
     */
    private EnumUseAttributeSerializer(Class<? extends Enum<?>> enumType) {
        this.enumType = enumType;
    }

    /**
     * Serializes an enum value to JSON.
     * <p>
     * If the enum value is annotated with {@link EnumUseAttributeInMarshalling},
     * the specified attribute's value will be used instead of the enum name.
     *
     * @param value       The enum value to serialize
     * @param gen         The JSON generator
     * @param serializers The serializer provider
     * @throws IOException If an I/O error occurs
     */
    @Override
    public void serialize(Enum<?> value, JsonGenerator gen, SerializerProvider serializers)
            throws IOException {
        if (value == null) {
            gen.writeNull();
            return;
        }

        Class<? extends Enum<?>> targetEnumType = enumType != null
                ? enumType
                : (Class<? extends Enum<?>>) value.getClass();

        EnumUseAttributeInMarshalling use = getEnumUseAttributeInMarshallingAnnotation(value, targetEnumType);
        String attr = Optional.ofNullable(use)
                .map(EnumMarshallingUtil::getAttributeName)
                .orElse(null);

        var attributeValue = getAttributeValue(value, attr, targetEnumType);

        if (isValidEnum(value, attr, attributeValue, targetEnumType)) {
            gen.writeString(attributeValue);
        } else {
            gen.writeString(value.name());
        }
    }

    /**
     * Creates a contextualized serializer for a specific property.
     * <p>
     * This method determines the actual enum type to use for serialization.
     * It handles both cases where the enum is a property of an object or serialized directly.
     *
     * @param prov     The serializer provider
     * @param property The bean property being serialized (can be null for direct enum serialization)
     * @return A serializer for the specific property type
     * @throws JsonMappingException If there is an error creating the contextualized serializer
     */
    @Override
    public JsonSerializer<?> createContextual(SerializerProvider prov, BeanProperty property)
            throws JsonMappingException {

        if (property == null) {
            log.debug("No property context available, will determine enum type dynamically during serialization");
            return this;
        }

        JavaType type = property.getType();
        Class<?> raw = type.getRawClass();

        if (raw != null && raw.isEnum()) {
            return new EnumUseAttributeSerializer((Class<? extends Enum<?>>) raw);
        }

        return prov.findValueSerializer(type);
    }
}
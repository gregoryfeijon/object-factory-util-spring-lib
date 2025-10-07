package io.github.gregoryfeijon.config.jackson.serialization.serializer;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.BeanProperty;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.ContextualSerializer;
import io.github.gregoryfeijon.config.jackson.serialization.JacksonSerializationHelper;
import io.github.gregoryfeijon.domain.annotation.EnumUseAttributeInMarshalling;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.util.Optional;

import static io.github.gregoryfeijon.config.jackson.serialization.JacksonSerializationHelper.getAttributeValue;
import static io.github.gregoryfeijon.config.jackson.serialization.JacksonSerializationHelper.getEnumUseAttributeInMarshallingAnnotation;
import static io.github.gregoryfeijon.config.jackson.serialization.JacksonSerializationHelper.isValidEnum;

/**
 * A Jackson serializer for enum types that supports serializing based on enum attributes.
 * <p>
 * This serializer uses the {@link EnumUseAttributeInMarshalling} annotation to determine
 * which attribute value should be used for serialization instead of the enum name.
 *
 * @author gregory.feijon
 */
@Slf4j
public class EnumUseAttributeSerializer extends JsonSerializer<Enum<?>> implements ContextualSerializer {

    /**
     * The enum class this deserializer handles.
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
     * @param value The enum value to serialize
     * @param gen The JSON generator
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
        EnumUseAttributeInMarshalling use = getEnumUseAttributeInMarshallingAnnotation(value, enumType);
        String attr = Optional.ofNullable(use)
                .map(JacksonSerializationHelper::getAttributeName)
                .orElse(null);
        var attributeValue = getAttributeValue(value, attr, enumType);
        if (isValidEnum(value, attr, attributeValue, enumType)) {
            gen.writeString(attributeValue);
        } else {
            gen.writeString(value.name());
        }
    }

    /**
     * Creates a contextualized serializer for a specific property.
     * <p>
     * This method determines the actual enum type to use for serialization.
     *
     * @param prov The serializer provider
     * @param property The bean property being serialized
     * @return A serializer for the specific property type
     * @throws JsonMappingException If there is an error creating the contextualized serializer
     */
    @Override
    public JsonSerializer<?> createContextual(SerializerProvider prov, BeanProperty property) throws JsonMappingException {
        if (property != null) {
            JavaType type = property.getType();
            Class<?> raw = type.getRawClass();
            if (raw != null && raw.isEnum()) {
                return new EnumUseAttributeSerializer((Class<? extends Enum<?>>) raw);
            }
            return prov.findValueSerializer(type);
        }

        throw JsonMappingException.from(prov.getGenerator(), "EnumUseAttributeSerializer cannot be used without BeanProperty");
    }
}
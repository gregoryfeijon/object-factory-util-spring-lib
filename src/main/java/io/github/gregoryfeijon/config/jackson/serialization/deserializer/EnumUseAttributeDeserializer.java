package io.github.gregoryfeijon.config.jackson.serialization.deserializer;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.BeanProperty;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.deser.ContextualDeserializer;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import io.github.gregoryfeijon.config.jackson.serialization.JacksonSerializationHelper;
import io.github.gregoryfeijon.domain.annotation.EnumUseAttributeInMarshalling;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.util.Optional;

import static io.github.gregoryfeijon.config.jackson.serialization.JacksonSerializationHelper.getEnumUseAttributeInMarshallingAnnotation;
import static io.github.gregoryfeijon.config.jackson.serialization.JacksonSerializationHelper.isValidEnum;

/**
 * A Jackson deserializer for enum types that supports deserializing based on enum attributes.
 * <p>
 * This deserializer uses the {@link EnumUseAttributeInMarshalling} annotation to determine
 * which attribute should be used for matching during deserialization.
 *
 * @author gregory.feijon
 */
@Slf4j
public class EnumUseAttributeDeserializer extends StdDeserializer<Enum<?>> implements ContextualDeserializer {

    /**
     * The enum class this deserializer handles.
     */
    private final Class<? extends Enum<?>> enumType;

    /**
     * Default constructor.
     * <p>
     * This constructor is needed for the deserializer to be registered as a module.
     * The actual enum type will be determined during contextualization.
     */
    public EnumUseAttributeDeserializer() {
        super(Enum.class);
        this.enumType = null;
    }

    /**
     * Constructor with a specific enum type.
     *
     * @param enumType The enum class to deserialize
     */
    public EnumUseAttributeDeserializer(Class<? extends Enum<?>> enumType) {
        super(enumType);
        this.enumType = enumType;
    }

    /**
     * Deserializes a JSON value to an enum constant.
     * <p>
     * If the enum is annotated with {@link EnumUseAttributeInMarshalling},
     * the method will match the JSON value against the specified attribute's value.
     *
     * @param p The JSON parser
     * @param ctxt The deserialization context
     * @return The enum constant, or null if no match is found
     * @throws IOException If an I/O error occurs
     */
    @Override
    public Enum<?> deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
        String text = p.getValueAsString();
        if (text == null || enumType == null) {
            return null;
        }
        for (Enum<?> constant : enumType.getEnumConstants()) {
            EnumUseAttributeInMarshalling annotation = getEnumUseAttributeInMarshallingAnnotation(constant, enumType);
            String attr = Optional.ofNullable(annotation)
                    .map(JacksonSerializationHelper::getAttributeName)
                    .orElse(null);

            if (isValidEnum(constant, attr, text, enumType)) {
                return constant;
            }
        }
        return null;
    }

    /**
     * Creates a contextualized deserializer for a specific property.
     * <p>
     * This method determines the actual enum type to use for deserialization.
     *
     * @param ctxt The deserialization context
     * @param property The bean property being deserialized
     * @return A deserializer for the specific property type
     * @throws JsonMappingException If there is an error creating the contextualized deserializer
     */
    @Override
    public JsonDeserializer<?> createContextual(DeserializationContext ctxt, BeanProperty property) throws JsonMappingException {
        JavaType type = property != null
                ? property.getType()
                : ctxt.getContextualType();
        Class<?> raw = type.getRawClass();
        if (raw != null && raw.isEnum()) {
            return new EnumUseAttributeDeserializer((Class<? extends Enum<?>>) raw);
        }
        return ctxt.findNonContextualValueDeserializer(type);
    }
}
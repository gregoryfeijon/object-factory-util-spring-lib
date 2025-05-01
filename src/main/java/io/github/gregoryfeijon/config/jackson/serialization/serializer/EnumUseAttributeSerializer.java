package io.github.gregoryfeijon.config.jackson.serialization.serializer;

import io.github.gregoryfeijon.config.jackson.serialization.JacksonSerializationHelper;
import io.github.gregoryfeijon.domain.annotation.EnumUseAttributeInMarshalling;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.BeanProperty;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.ContextualSerializer;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.util.Optional;

import static io.github.gregoryfeijon.config.jackson.serialization.JacksonSerializationHelper.getAttributeValue;
import static io.github.gregoryfeijon.config.jackson.serialization.JacksonSerializationHelper.getEnumUseAttributeInMarshallingAnnotation;
import static io.github.gregoryfeijon.config.jackson.serialization.JacksonSerializationHelper.isValidEnum;

@Slf4j
public class EnumUseAttributeSerializer extends JsonSerializer<Enum<?>> implements ContextualSerializer {

    private final Class<? extends Enum<?>> enumType;

    public EnumUseAttributeSerializer() {
        this.enumType = null;
    }

    private EnumUseAttributeSerializer(Class<? extends Enum<?>> enumType) {
        this.enumType = enumType;
    }

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

        throw JsonMappingException.from(prov.getGenerator(), "EnumUseAttributeSerializer não pode ser usado sem BeanProperty");
    }
}

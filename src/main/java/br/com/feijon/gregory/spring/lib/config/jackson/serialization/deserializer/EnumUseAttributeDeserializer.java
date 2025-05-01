package br.com.feijon.gregory.spring.lib.config.jackson.serialization.deserializer;

import br.com.feijon.gregory.spring.lib.config.jackson.serialization.JacksonSerializationHelper;
import br.com.feijon.gregory.spring.lib.domain.annotation.EnumUseAttributeInMarshalling;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.BeanProperty;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.deser.ContextualDeserializer;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.util.Optional;

import static br.com.feijon.gregory.spring.lib.config.jackson.serialization.JacksonSerializationHelper.getEnumUseAttributeInMarshallingAnnotation;
import static br.com.feijon.gregory.spring.lib.config.jackson.serialization.JacksonSerializationHelper.isValidEnum;

@Slf4j
public class EnumUseAttributeDeserializer extends StdDeserializer<Enum<?>> implements ContextualDeserializer {

    private final Class<? extends Enum<?>> enumType;

    public EnumUseAttributeDeserializer() {
        super(Enum.class);
        this.enumType = null;
    }

    public EnumUseAttributeDeserializer(Class<? extends Enum<?>> enumType) {
        super(enumType);
        this.enumType = enumType;
    }

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
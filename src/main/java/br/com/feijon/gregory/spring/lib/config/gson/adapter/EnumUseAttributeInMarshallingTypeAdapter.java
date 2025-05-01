package br.com.feijon.gregory.spring.lib.config.gson.adapter;

import br.com.feijon.gregory.spring.lib.domain.annotation.EnumUseAttributeInMarshalling;
import com.google.gson.TypeAdapter;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;
import com.google.gson.stream.JsonWriter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.ReflectionUtils;
import org.springframework.util.StringUtils;

import java.io.IOException;
import java.lang.reflect.Field;
import java.util.Objects;
import java.util.Optional;

@Slf4j
@RequiredArgsConstructor
public class EnumUseAttributeInMarshallingTypeAdapter<T> extends TypeAdapter<T> {

    private final Class<? super T> enumClass;

    public EnumUseAttributeInMarshallingTypeAdapter(TypeToken<T> type) {
        this.enumClass = type.getRawType();
    }

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

    private EnumUseAttributeInMarshalling getEnumUseAttributeInMarshallingAnnotation(Enum<?> value) {
        try {
            Field field = enumClass.getField(value.name());
            return field.getAnnotation(EnumUseAttributeInMarshalling.class);
        } catch (NoSuchFieldException e) {
            log.warn("Não foi encontrado um campo com o nome especificado");
        }
        return null;
    }

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

    private boolean isValidEnum(Enum<?> enumValue, String attributeName, String attributeValue) {
        if (!StringUtils.hasText(attributeName)) {
            return enumValue.name().equals(attributeValue);
        } else {
            String attributeValueFromEnum = getAttributeValue(enumValue, attributeName);
            return Objects.equals(attributeValueFromEnum, attributeValue);
        }
    }

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


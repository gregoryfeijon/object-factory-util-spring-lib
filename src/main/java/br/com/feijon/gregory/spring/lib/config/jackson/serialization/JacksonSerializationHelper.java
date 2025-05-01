package br.com.feijon.gregory.spring.lib.config.jackson.serialization;

import br.com.feijon.gregory.spring.lib.domain.annotation.EnumUseAttributeInMarshalling;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.ReflectionUtils;
import org.springframework.util.StringUtils;

import java.lang.reflect.Field;
import java.util.Objects;

@Slf4j
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class JacksonSerializationHelper {

    public static EnumUseAttributeInMarshalling getEnumUseAttributeInMarshallingAnnotation(Enum<?> value, Class<? extends Enum<?>> enumType) {
        if (enumType == null) {
            throw new IllegalStateException("enumType não deve ser nulo após contextualização");
        }
        try {
            Field field = enumType.getField(value.name());
            return field.getAnnotation(EnumUseAttributeInMarshalling.class);
        } catch (NoSuchFieldException e) {
            log.warn("Não foi encontrado um campo com o nome especificado: {}", value.name(), e);
        }
        return null;
    }

    public static String getAttributeName(EnumUseAttributeInMarshalling useAttribute) {
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

    public static boolean isValidEnum(Enum<?> enumValue, String attributeName, String attributeValue, Class<? extends Enum<?>> enumType) {
        if (!StringUtils.hasText(attributeName)) {
            return enumValue.name().equals(attributeValue);
        } else {
            String attributeValueFromEnum = getAttributeValue(enumValue, attributeName, enumType);
            return Objects.equals(attributeValueFromEnum, attributeValue);
        }
    }

    public static String getAttributeValue(Enum<?> value, String attributeName, Class<? extends Enum<?>> enumType) {
        if (value != null && attributeName != null) {
            if (enumType == null) {
                throw new IllegalStateException("enumType não deve ser nulo após contextualização");
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

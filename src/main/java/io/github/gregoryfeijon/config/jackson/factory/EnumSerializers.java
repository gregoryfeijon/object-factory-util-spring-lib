package io.github.gregoryfeijon.config.jackson.factory;

import io.github.gregoryfeijon.config.jackson.serialization.serializer.EnumUseAttributeSerializer;
import com.fasterxml.jackson.databind.BeanDescription;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializationConfig;
import com.fasterxml.jackson.databind.ser.Serializers;
import org.springframework.stereotype.Component;

@Component
public class EnumSerializers extends Serializers.Base {

    @Override
    public JsonSerializer<?> findSerializer(SerializationConfig config, JavaType type, BeanDescription beanDesc) {
        if (type.isEnumType()) {
            return new EnumUseAttributeSerializer();
        }
        return null;
    }
}
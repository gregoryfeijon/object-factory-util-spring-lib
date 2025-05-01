package io.github.gregoryfeijon.config.jackson.factory;

import io.github.gregoryfeijon.config.jackson.serialization.deserializer.EnumUseAttributeDeserializer;
import com.fasterxml.jackson.databind.BeanDescription;
import com.fasterxml.jackson.databind.DeserializationConfig;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.deser.Deserializers;
import org.springframework.stereotype.Component;

@Component
public class EnumDeserializers extends Deserializers.Base {

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
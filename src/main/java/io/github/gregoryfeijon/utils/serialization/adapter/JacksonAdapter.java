package io.github.gregoryfeijon.utils.serialization.adapter;

import io.github.gregoryfeijon.exception.ApiException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.core.serializer.support.SerializationFailedException;

import java.lang.reflect.Type;

public non-sealed class JacksonAdapter implements SerializerAdapter {

    private final ObjectMapper mapper;

    public JacksonAdapter(ObjectMapper mapper) {
        this.mapper = mapper;
    }

    @Override
    public String serialize(Object object) throws SerializationFailedException {
        try {
            return mapper.writeValueAsString(object);
        } catch (JsonProcessingException e) {
            throw new ApiException("Serialization Failed", e);
        }
    }

    @Override
    public String serialize(Object object, Type type) throws SerializationFailedException {
        try {
            JavaType javaType = mapper.constructType(type);
            return mapper.writerFor(javaType).writeValueAsString(object);
        } catch (JsonProcessingException e) {
            throw new SerializationFailedException("Error while trying to serialize object with specific type!", e);
        }
    }

    @Override
    public <T> T deserialize(String json, Class<T> type) throws SerializationFailedException {
        try {
            return mapper.readValue(json, type);
        } catch (JsonProcessingException e) {
            throw new ApiException("unserialization failed", e);
        }
    }

    @Override
    public <T> T deserialize(String json, Type type) throws SerializationFailedException {
        try {
            return mapper.readValue(json, mapper.constructType(type));
        } catch (JsonProcessingException e) {
            throw new SerializationFailedException("Error while trying to unserialize object with specific type!", e);
        }
    }
}
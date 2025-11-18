package io.github.gregoryfeijon.object.factory.util.utils.serialization.adapter;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.gregoryfeijon.object.factory.util.exception.ApiException;
import org.springframework.core.serializer.support.SerializationFailedException;

import java.lang.reflect.Type;

/**
 * Jackson implementation of the SerializerAdapter interface.
 * <p>
 * This adapter uses Jackson's ObjectMapper for serialization and deserialization operations.
 *
 * @author gregory.feijon
 */
public non-sealed class JacksonAdapter implements SerializerAdapter {

    private final ObjectMapper mapper;

    /**
     * Constructs a new adapter with the specified ObjectMapper instance.
     *
     * @param mapper The ObjectMapper instance to use for serialization operations
     */
    public JacksonAdapter(ObjectMapper mapper) {
        this.mapper = mapper;
    }

    /**
     * {@inheritDoc}
     *
     * @throws ApiException If serialization fails
     */
    @Override
    public String serialize(Object object) throws SerializationFailedException {
        try {
            return mapper.writeValueAsString(object);
        } catch (JsonProcessingException e) {
            throw new ApiException("Serialization Failed", e);
        }
    }

    /**
     * {@inheritDoc}
     *
     * @throws SerializationFailedException If serialization fails
     */
    @Override
    public String serialize(Object object, Type type) throws SerializationFailedException {
        try {
            JavaType javaType = mapper.constructType(type);
            return mapper.writerFor(javaType).writeValueAsString(object);
        } catch (JsonProcessingException e) {
            throw new SerializationFailedException("Error while trying to serialize object with specific type!", e);
        }
    }

    /**
     * {@inheritDoc}
     *
     * @throws ApiException If deserialization fails
     */
    @Override
    public <T> T deserialize(String json, Class<T> type) throws SerializationFailedException {
        try {
            return mapper.readValue(json, type);
        } catch (JsonProcessingException e) {
            throw new ApiException("Deserialization failed", e);
        }
    }

    /**
     * {@inheritDoc}
     *
     * @throws SerializationFailedException If deserialization fails
     */
    @Override
    public <T> T deserialize(String json, Type type) throws SerializationFailedException {
        try {
            return mapper.readValue(json, mapper.constructType(type));
        } catch (JsonProcessingException e) {
            throw new SerializationFailedException("Error while trying to deserialize object with specific type!", e);
        }
    }
}
package io.github.gregoryfeijon.object.factory.util.utils.serialization.adapter;

import com.google.gson.Gson;

import java.lang.reflect.Type;

/**
 * Gson implementation of the SerializerAdapter interface.
 * <p>
 * This adapter uses Google's Gson library for serialization and deserialization operations.
 *
 * @author gregory.feijon
 */
public non-sealed class GsonAdapter implements SerializerAdapter {

    private final Gson gson;

    /**
     * Constructs a new adapter with the specified Gson instance.
     *
     * @param gson The Gson instance to use for serialization operations
     */
    public GsonAdapter(Gson gson) {
        this.gson = gson;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String serialize(Object object) {
        return gson.toJson(object);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String serialize(Object object, Type type) {
        return gson.toJson(object, type);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public <T> T deserialize(String json, Class<T> type) {
        return gson.fromJson(json, type);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public <T> T deserialize(String json, Type type) {
        return gson.fromJson(json, type);
    }
}
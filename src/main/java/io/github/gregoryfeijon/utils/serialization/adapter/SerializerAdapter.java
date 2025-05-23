package io.github.gregoryfeijon.utils.serialization.adapter;

import java.lang.reflect.Type;

/**
 * Interface defining methods for serialization and deserialization operations.
 * <p>
 * This interface provides a unified way to handle serialization operations
 * regardless of the underlying serialization framework (Gson, Jackson, etc.).
 *
 * @author gregory.feijon
 */
public sealed interface SerializerAdapter permits GsonAdapter, JacksonAdapter {

    /**
     * Serializes an object to a JSON string.
     *
     * @param object The object to serialize
     * @return The JSON string representation of the object
     */
    String serialize(Object object);

    /**
     * Serializes an object to a JSON string using a specific type.
     * <p>
     * This method is useful for serializing objects with generic types
     * where type information needs to be explicitly provided.
     *
     * @param object The object to serialize
     * @param type The specific type to use for serialization
     * @return The JSON string representation of the object
     */
    String serialize(Object object, Type type);

    /**
     * Deserializes a JSON string to an object of the specified class.
     *
     * @param <T> The type to deserialize to
     * @param json The JSON string to deserialize
     * @param type The class of the target object
     * @return The deserialized object
     */
    <T> T deserialize(String json, Class<T> type);

    /**
     * Deserializes a JSON string to an object of the specified type.
     * <p>
     * This method is useful for deserializing objects with generic types
     * where type information needs to be explicitly provided.
     *
     * @param <T> The type to deserialize to
     * @param json The JSON string to deserialize
     * @param type The specific type to use for deserialization
     * @return The deserialized object
     */
    <T> T deserialize(String json, Type type);
}
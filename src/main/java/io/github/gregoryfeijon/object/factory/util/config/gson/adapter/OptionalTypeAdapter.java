package io.github.gregoryfeijon.object.factory.util.config.gson.adapter;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonNull;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import org.springframework.stereotype.Component;

import java.lang.reflect.Type;
import java.util.Optional;

/**
 * A Gson type adapter for handling Java's Optional type.
 * <p>
 * This adapter allows Gson to properly serialize Optional values by unwrapping the contained
 * value during serialization and wrapping values back into Optionals during deserialization.
 *
 * @author gregory.feijon
 */
@Component
public class OptionalTypeAdapter implements JsonDeserializer<Optional<?>>, JsonSerializer<Optional<?>> {

    /**
     * Serializes an Optional value for Gson.
     * <p>
     * If the Optional is present, serializes its contained value.
     * If the Optional is empty, serializes as JSON null.
     *
     * @param src The Optional to serialize
     * @param typeOfSrc The type of the source object
     * @param context The serialization context
     * @return The JsonElement representation of the Optional
     */
    @Override
    public JsonElement serialize(Optional<?> src, Type typeOfSrc, JsonSerializationContext context) {
        return src.isPresent() ? context.serialize(src.get()) : JsonNull.INSTANCE;
    }

    /**
     * Deserializes a JSON element into an Optional.
     * <p>
     * If the JSON element is null, returns an empty Optional.
     * Otherwise, deserializes the element and wraps it in an Optional.
     *
     * @param json The JSON element to deserialize
     * @param typeOfT The type of the object to deserialize
     * @param context The deserialization context
     * @return An Optional containing the deserialized value, or empty if the JSON was null
     * @throws JsonParseException If there is an error during deserialization
     */
    @Override
    public Optional<?> deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        return json.isJsonNull()
                ? Optional.empty()
                : Optional.ofNullable(context.deserialize(json,
                ((java.lang.reflect.ParameterizedType) typeOfT).getActualTypeArguments()[0]));
    }
}
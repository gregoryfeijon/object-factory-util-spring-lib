package io.github.gregoryfeijon.config.gson.adapter;

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

@Component
public class OptionalTypeAdapter implements JsonDeserializer<Optional<?>>, JsonSerializer<Optional<?>> {

    @Override
    public JsonElement serialize(Optional<?> src, Type typeOfSrc, JsonSerializationContext context) {
        return src.isPresent() ? context.serialize(src.get()) : JsonNull.INSTANCE;
    }

    @Override
    public Optional<?> deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        return json.isJsonNull()
                ? Optional.empty()
                : Optional.ofNullable(context.deserialize(json,
                ((java.lang.reflect.ParameterizedType) typeOfT).getActualTypeArguments()[0]));
    }
}
